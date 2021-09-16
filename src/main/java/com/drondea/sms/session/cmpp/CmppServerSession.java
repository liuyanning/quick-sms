package com.drondea.sms.session.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.conf.cmpp.CmppServerSocketConfig;
import com.drondea.sms.handler.TailHandler;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.handler.transcoder.Cmpp20MessageCodec;
import com.drondea.sms.handler.transcoder.Cmpp30MessageCodec;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.*;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.UserChannelConfig;
import com.google.common.primitives.Ints;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: cmpp的服务器端处理，接受客户端连接，鉴权等
 * @author: 刘彦宁
 * @date: 2020年06月10日15:39
 **/
public class CmppServerSession extends AbstractServerSession {

    private static final Logger logger = LoggerFactory.getLogger(CmppServerSession.class);

    /**
     * 创建session管理器
     *
     * @param sessionManager
     */
    public CmppServerSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        super(ctx, sessionManager);
        setSessionType("CMPP");
    }

    @Override
    public void dealConnectRequest(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof CmppConnectRequestMessage)) {
            return;
        }

        CmppConnectRequestMessage msg = (CmppConnectRequestMessage) message;
        String sourceAddr = msg.getSourceAddr();
        AbstractServerSessionManager sessionManager = (AbstractServerSessionManager) getSessionManager();
        byte[] authenticatorSource = msg.getAuthenticatorSource();
        long timestamp = msg.getTimestamp();
        UserChannelConfig userChannelConfig = sessionManager.getUserChannelConfig(sourceAddr);
        short version = msg.getVersion();
        if (userChannelConfig == null || !validIpAddress(userChannelConfig, getChannel())) {
            //登录失败，用户不存在或者IP不合法
            sendLoginFailed(msg, userChannelConfig, 2, version);
            return;
        }
        //设置用户名
        setUserName(userChannelConfig.getUserName());
        userChannelConfig.setVersion(version);

        ICustomHandler customHandler = sessionManager.getCustomHandler();
        boolean customValidResult = true;
        if (customHandler != null) {
            customValidResult = customHandler.customLoginValidate(msg, userChannelConfig, getChannel());
        }

        int validResult = validClientMsg(authenticatorSource, timestamp, userChannelConfig);

        logger.debug("登录结果 {}, {}", validResult, customValidResult);
        //验证成功
        if (customValidResult && validResult == 0) {
            //添加session说明超过最大的连接限制数
            boolean addResult = sessionManager.addUserSession(userChannelConfig, this);
            logger.debug("校验成功,添加用户, {}", addResult);
            if (addResult) {

                CmppServerSocketConfig socketConfig = (CmppServerSocketConfig) getConfiguration();
                short serverVersion = socketConfig.getVersion();
                //客户端设置的版本号不一致，适应客户端
                if (serverVersion == CmppConstants.VERSION_20 && version >= CmppConstants.VERSION_30) {
                    changeVersion(getChannel(), CmppConstants.VERSION_30);
                    serverVersion = CmppConstants.VERSION_30;
                }
                if (serverVersion == CmppConstants.VERSION_30 && version < CmppConstants.VERSION_30) {
                    changeVersion(getChannel(), CmppConstants.VERSION_20);
                    serverVersion = CmppConstants.VERSION_20;
                }
                setState(STATE_LOGIN_SUCCESS);
                sendLoginSuccess(msg, userChannelConfig, serverVersion);

                //登录成功后的处理
                doAfterLogin(userChannelConfig);

                //增加业务处理
                addBIZHandler();
                return;
            } else {
                //超过限制了失败
                validResult = 5;
            }

        } else {
            //认证错误或者自定义校验错误
            validResult = 3;
        }

        failedLogin(msg, validResult);

        //登录失败
        sendLoginFailed(msg, userChannelConfig, validResult, version);
    }

    private void sendLoginSuccess(CmppConnectRequestMessage msg, UserChannelConfig userChannelConfig, short version) {
        CmppConnectResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, 0, version);
        sendMessage(responseMessage);
    }

    private CmppConnectResponseMessage createConnectResponseMsg(CmppConnectRequestMessage msg, UserChannelConfig userChannelConfig, int status, short version) {
        CmppConnectResponseMessage responseMessage = new CmppConnectResponseMessage();
        responseMessage.getHeader().setSequenceId(msg.getHeader().getSequenceId());
        responseMessage.setStatus(status);
        responseMessage.setVersion(version);
        if (status == 0) {
            byte[] bytes = DigestUtils.md5(CommonUtil.concat(
                    Ints.toByteArray((int) responseMessage.getStatus()), msg.getAuthenticatorSource(), userChannelConfig
                            .getPassword().getBytes(StandardCharsets.ISO_8859_1)));
            responseMessage.setAuthenticatorISMG(bytes);
        } else {
            responseMessage.setAuthenticatorISMG(new byte[16]);
        }

        return responseMessage;
    }

    private void sendLoginFailed(CmppConnectRequestMessage msg, UserChannelConfig userChannelConfig,
                                 int status, short version) {
        CmppConnectResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, status, version);
        ChannelFuture promise = sendMessage(responseMessage);
        final ChannelSession session = this;
        //关闭channel
        promise.addListener(future -> session.close());
    }

    private int validClientMsg(byte[] authenticatorSource, long timestamp, UserChannelConfig userChannelConfig) {
        byte[] userBytes = userChannelConfig.getUserName().getBytes(StandardCharsets.ISO_8859_1);
        byte[] passwdBytes = userChannelConfig.getPassword().getBytes(StandardCharsets.ISO_8859_1);

        byte[] timestampBytes = String.format("%010d", timestamp).getBytes(StandardCharsets.ISO_8859_1);
        byte[] authBytes = DigestUtils.md5(CommonUtil.concat(userBytes, new byte[9], passwdBytes, timestampBytes));
        if (Arrays.equals(authBytes, authenticatorSource)) {
            return 0;
        } else {
            logger.error("AuthenticatorSource valided failed.s:{},c:{}", Hex.encodeHexString(authBytes), Hex.encodeHexString(authenticatorSource));
            return 3;
        }
    }


    /**
     * 添加业务处理的handler
     */
    private void addBIZHandler() {
        logger.debug("添加业务处理器");

        Channel channel = getChannel();
        //channel绑定session对象
        channel.attr(CmppConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("ServerSessionFilterHandler", CmppConstants.SERVER_SESSION_FILTER_HANDLER);

        ServerSocketConfig connConf = getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 30 : connConf.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("CmppIdleStateHandler", CmppConstants.IDLE_STATE_HANDLER);

        //长短信解析处理
        pipeline.addLast("SubmitLongMessageHandler", CmppConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", CmppConstants.DELIVER_LONG_MESSAGE_HANDLER);

        SessionManager sessionManager = getSessionManager();

        AbstractServerSessionManager serverSessionManager = (AbstractServerSessionManager) sessionManager;
        AbstractCounterLimitHandler counterLimitHandler = serverSessionManager.getCounterLimitHandler(getUserName());
        if (counterLimitHandler != null) {
            pipeline.addLast("ServerCounterLimitHandler", counterLimitHandler);
            pipeline.addLast("ServerMetricsMeterHandler", GlobalConstants.SERVER_METRICS_METER_HANDLER);
        }

        pipeline.addLast("ActiveTestRequestHandler", CmppConstants.ACTIVE_TEST_REQUEST_HANDLER);
        pipeline.addLast("ActiveTestResponseHandler", CmppConstants.ACTIVE_TEST_RESPONSE_HANDLER);
        pipeline.addLast("TerminateRequestHandler", CmppConstants.TERMINATE_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("TerminateResponseHandler", CmppConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("CmppUserEventHandler", CmppConstants.CMPP_USER_EVENT_HANDLER);

        //自定义pipeline处理器
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.configPipelineAfterLogin(pipeline);
        }
//        pipeline.addLast("NettyTailHandler", GlobalConstants.TAIL_HANDLER);
        notifyChannelLoginSuccess(channel);
    }

    /**
     * 根据版本号修改编解码handler
     *
     * @param channel
     * @param version
     */
    private void changeVersion(Channel channel, short version) {
        logger.debug("change to cmpp version {}", version);
        if (version == CmppConstants.VERSION_20) {
            channel.pipeline().replace("CmppMessageCodec", "Cmpp20MessageCodec", Cmpp20MessageCodec.getInstance());
        } else {
            channel.pipeline().replace("CmppMessageCodec", "Cmpp30MessageCodec", Cmpp30MessageCodec.getInstance());
        }

    }

    @Override
    public void countTXMessage(IMessage message) {
        CmppSessionCounters counters = (CmppSessionCounters) getCounters();

        AbstractCmppMessage msg = (AbstractCmppMessage) message;
        counters.countTXMessage(msg);
    }

    @Override
    public void countRXMessage(IMessage message) {
        CmppSessionCounters counters = (CmppSessionCounters) getCounters();

        AbstractCmppMessage msg = (AbstractCmppMessage) message;
        counters.countRXMessage(msg);
    }


    @Override
    public void fireExceptionThrown(Throwable t) {

    }

    @Override
    protected boolean needSendLater(IMessage request, IMessage response) {
        if (response instanceof CmppSubmitResponseMessage) {
            CmppSubmitResponseMessage submitResp = (CmppSubmitResponseMessage) response;

            if ((submitResp.getResult() != 0L) && (submitResp.getResult() != 8L)) {
                logger.error("Receive Err Response result: {} . Req: {} ,Resp:{}",submitResp.getResult(), request, submitResp);
            }

            return submitResp.getResult() == 8L;
        } else if (response instanceof CmppDeliverResponseMessage) {
            CmppDeliverResponseMessage deliverResp = (CmppDeliverResponseMessage) response;

            if ((deliverResp.getResult() != 0L) && (deliverResp.getResult() != 8L)) {
                logger.error("Receive Err Response result: {} . Req: {} ,Resp:{}",deliverResp.getResult(), request, deliverResp);
            }

            return deliverResp.getResult() == 8L;
        }
        return false;
    }
}
