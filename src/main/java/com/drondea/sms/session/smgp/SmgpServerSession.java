package com.drondea.sms.session.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.AbstractSmgpMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectResponseMessage;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.SmgpConstants;
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
 * @version V3.0
 * @description: smgp的服务器端处理，接受客户端连接，鉴权等
 * @author: ywj
 * @date: 2020年06月10日15:39
 **/
public class SmgpServerSession extends AbstractServerSession {

    private static final Logger logger = LoggerFactory.getLogger(SmgpServerSession.class);

    /**
     * 创建session管理器
     *
     * @param sessionManager
     */
    public SmgpServerSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        super(ctx, sessionManager);
        setSessionType("SMGP");
    }

    @Override
    public void dealConnectRequest(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof SmgpConnectRequestMessage)) {
            return;
        }

        SmgpConnectRequestMessage msg = (SmgpConnectRequestMessage) message;
        String clientId = msg.getClientId();
        AbstractServerSessionManager sessionManager = (AbstractServerSessionManager) getSessionManager();
        byte[] authenticatorSource = msg.getAuthenticatorClient();
        long timestamp = msg.getTimestamp();
        UserChannelConfig userChannelConfig = sessionManager.getUserChannelConfig(clientId);
        short version = msg.getClientVersion();
        if (userChannelConfig == null) {
            //登录失败
            sendLoginFailed(msg, null, 20, version);
            return;
        }
        setUserName(userChannelConfig.getUserName());

        ICustomHandler customHandler = sessionManager.getCustomHandler();
        boolean customValidResult = true;
        if (customHandler != null) {
            customValidResult = customHandler.customLoginValidate(msg, userChannelConfig, getChannel());
        }

        int validResult = validClientMsg(authenticatorSource, timestamp, userChannelConfig);
        boolean isValidIp = validIpAddress(userChannelConfig, getChannel());
        logger.debug("登录结果 {}, {}", validResult, customValidResult);
        //验证成功
        if (customValidResult && validResult == 0 && isValidIp) {
            //添加session说明超过最大的连接限制数
            boolean addResult = sessionManager.addUserSession(userChannelConfig, this);
            logger.debug("校验成功,添加用户, {}", addResult);
            if (addResult) {
                setState(STATE_LOGIN_SUCCESS);

                sendLoginSuccess(msg, userChannelConfig, version);
                //只有2和3才推送状态和MO
                boolean sendDeliver = msg.getLoginMode() > 0;
                //登录成功后的处理
                doAfterLogin(userChannelConfig, sendDeliver);

                //增加业务处理
                addBIZHandler();
                return;
            } else {
                //超过限制了失败
                validResult = 2;
            }
        } else {
            validResult = 21;
        }
        //IP不合法错误码23
        if (!isValidIp) {
            validResult = 23;
        }
        failedLogin(userChannelConfig, msg, validResult);
        //登录失败
        sendLoginFailed(msg, userChannelConfig, validResult, version);
    }

    private void sendLoginSuccess(SmgpConnectRequestMessage msg, UserChannelConfig userChannelConfig, short version) {
        SmgpConnectResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, 0, version);
        sendMessage(responseMessage);
    }

    private SmgpConnectResponseMessage createConnectResponseMsg(SmgpConnectRequestMessage msg, UserChannelConfig userChannelConfig, int status, short version) {
        SmgpConnectResponseMessage responseMessage = new SmgpConnectResponseMessage();
        responseMessage.getHeader().setSequenceId(msg.getSequenceId());
        responseMessage.setStatus(status);
        responseMessage.setServerVersion((byte) version);
        if (status == 0) {
            byte[] bytes = DigestUtils.md5(CommonUtil.concat(
                    Ints.toByteArray((int) responseMessage.getStatus()), msg.getAuthenticatorClient(), userChannelConfig
                            .getPassword().getBytes(StandardCharsets.ISO_8859_1)));
            responseMessage.setAuthenticatorServer(bytes);
        } else {
            responseMessage.setAuthenticatorServer(new byte[16]);
        }

        return responseMessage;
    }

    private void sendLoginFailed(SmgpConnectRequestMessage msg, UserChannelConfig userChannelConfig,
                                 int status, short version) {
        SmgpConnectResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, status, version);
        ChannelFuture promise = sendMessage(responseMessage);
        final ChannelSession session = this;
        //关闭channel
        promise.addListener(future -> session.close());
    }

    private int validClientMsg(byte[] authenticatorSource, long timestamp, UserChannelConfig userChannelConfig) {
        byte[] userBytes = userChannelConfig.getUserName().getBytes(StandardCharsets.ISO_8859_1);
        byte[] passwdBytes = userChannelConfig.getPassword().getBytes(StandardCharsets.ISO_8859_1);

        byte[] timestampBytes = String.format("%010d", timestamp).getBytes(StandardCharsets.ISO_8859_1);
        byte[] authBytes = DigestUtils.md5(CommonUtil.concat(userBytes, new byte[7], passwdBytes, timestampBytes));
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
        channel.attr(SmgpConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("ServerSessionFilterHandler", SmgpConstants.SERVER_SESSION_FILTER_HANDLER);

        UserChannelConfig userChannelConfig = getUserChannelConfig();
        int idleTime = userChannelConfig.getIdleTime() == 0 ? 30 : userChannelConfig.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SmgpIdleStateHandler", SmgpConstants.IDLE_STATE_HANDLER);

        //长短信解析处理
        pipeline.addLast("SubmitLongMessageHandler", SmgpConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", SmgpConstants.DELIVER_LONG_MESSAGE_HANDLER);

        SessionManager sessionManager = getSessionManager();

        AbstractServerSessionManager serverSessionManager = (AbstractServerSessionManager) sessionManager;
        AbstractCounterLimitHandler counterLimitHandler = serverSessionManager.getCounterLimitHandler(getUserName());
        if (counterLimitHandler != null) {
            pipeline.addLast("ServerCounterLimitHandler", counterLimitHandler);
            pipeline.addLast("ServerMetricsMeterHandler", GlobalConstants.SERVER_METRICS_METER_HANDLER);
        }

        pipeline.addLast("ActiveTestRequestHandler", SmgpConstants.ACTIVE_TEST_REQUEST_HANDLER);
        pipeline.addLast("ActiveTestResponseHandler", SmgpConstants.ACTIVE_TEST_RESPONSE_HANDLER);
        pipeline.addLast("TerminateRequestHandler", SmgpConstants.EXIT_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("TerminateResponseHandler", SmgpConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);
//
//        //用户事件处理器
        pipeline.addLast("SmgpUserEventHandler", SmgpConstants.SMGP_USER_EVENT_HANDLER);

        //自定义pipeline处理器
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.configPipelineAfterLogin(pipeline);
        }
//        pipeline.addLast("NettyTailHandler", GlobalConstants.TAIL_HANDLER);
        notifyChannelLoginSuccess(channel);
    }

    @Override
    public void countTXMessage(IMessage message) {
        SmgpSessionCounters counters = (SmgpSessionCounters) getCounters();

        AbstractSmgpMessage msg = (AbstractSmgpMessage) message;
        counters.countTXMessage(msg);
    }

    @Override
    public void countRXMessage(IMessage message) {
        SmgpSessionCounters counters = (SmgpSessionCounters) getCounters();

        AbstractSmgpMessage msg = (AbstractSmgpMessage) message;
        counters.countRXMessage(msg);
    }


    @Override
    public void fireExceptionThrown(Throwable t) {

    }

    @Override
    protected boolean needSendLater(IMessage request, IMessage response) {
        return false;
    }
}
