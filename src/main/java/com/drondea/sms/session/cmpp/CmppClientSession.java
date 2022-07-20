package com.drondea.sms.session.cmpp;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SystemClock;
import com.drondea.sms.handler.transcoder.Cmpp20MessageCodec;
import com.drondea.sms.handler.transcoder.Cmpp30MessageCodec;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.*;
import com.drondea.sms.conf.cmpp.CmppClientSocketConfig;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: cmpp的session管理
 * @author: 刘彦宁
 * @date: 2020年06月05日18:34
 **/
public class CmppClientSession extends AbstractClientSession {

    private static final Logger logger = LoggerFactory.getLogger(CmppClientSession.class);
    /**
     * 提交超速错误码
     */
    private static final short OVER_SPEED_CODE = 8;

    /**
     * 创建session管理器
     *
     * @param ctx
     * @param connectorManager
     */
    public CmppClientSession(ChannelHandlerContext ctx, SessionManager connectorManager) {
        super(ctx, connectorManager);
    }

    @Override
    protected void sendLoginMsg() {
        CmppClientSocketConfig connConf = (CmppClientSocketConfig) getConfiguration();
        CmppConnectRequestMessage requestMessage = new CmppConnectRequestMessage();
        requestMessage.getHeader().setSequenceId(getSequenceNumber().next());
        String userName = connConf.getUserName();
        if (StringUtils.isEmpty(userName)) {
            logger.error("userName cannot be empty");
            return;
        }
        requestMessage.setSourceAddr(userName);
        String timestamp = DateFormatUtils.format(SystemClock.now(), "MMddHHmmss");
        requestMessage.setTimestamp(Long.parseLong(timestamp));
        String password = connConf.getPassword();
        if (StringUtils.isEmpty(password)) {
            logger.error("password cannot be empty");
            return;
        }
        Charset charset = connConf.getCharset();
        if (charset == null) {
            charset = CmppConstants.DEFAULT_TRANSPORT_CHARSET;
        }
        requestMessage.setAuthenticatorSource(CommonUtil.getAuthenticatorSource(userName, password, timestamp, charset));
        short version = connConf.getVersion();
        if (version < CmppConstants.VERSION_30) {
            requestMessage.setVersion(CmppConstants.VERSION_20);
        } else {
            requestMessage.setVersion(CmppConstants.VERSION_30);
        }
        setState(STATE_LOGINING);
        //发送登录数据
        sendMessage(requestMessage);
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

        CmppClientSocketConfig connConf = (CmppClientSocketConfig) getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 40 : connConf.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("CmppIdleStateHandler", CmppConstants.IDLE_STATE_HANDLER);

        pipeline.addLast("SubmitLongMessageHandler", CmppConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", CmppConstants.DELIVER_LONG_MESSAGE_HANDLER);

        pipeline.addLast("ActiveTestRequestHandler", CmppConstants.ACTIVE_TEST_REQUEST_HANDLER);
        pipeline.addLast("ActiveTestResponseHandler", CmppConstants.ACTIVE_TEST_RESPONSE_HANDLER);
        pipeline.addLast("TerminateRequestHandler", CmppConstants.TERMINATE_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("TerminateResponseHandler", CmppConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("CmppUserEventHandler", CmppConstants.CMPP_USER_EVENT_HANDLER);

        //增加自定义的handler
        SessionManager sessionManager = getSessionManager();
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.configPipelineAfterLogin(pipeline);
        }
        pipeline.addLast("NettyTailHandler", GlobalConstants.TAIL_HANDLER);
        //发起一个事件，用户登录成功，可以发送短信了，要放在后边才能通知前边所有handler
        notifyChannelLoginSuccess(channel);

    }


    @Override
    public boolean dealConnectResponseMessage(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof CmppConnectResponseMessage)) {
            logger.error("用户登录却返回了:" + message);
            return false;
        }
        CmppConnectResponseMessage msg = (CmppConnectResponseMessage) message;
        CmppClientSocketConfig socketConfig = (CmppClientSocketConfig) getConfiguration();
        long status = msg.getStatus();
//        short version = msg.getVersion();
        boolean customLoginValid = customLoginValid(message);
        if (status == 0 && customLoginValid) {
            logger.info("{} login success", socketConfig.getId());
            setState(STATE_LOGIN_SUCCESS);
            //服务器返回的版本号不一致，适应服务器端
//            if (version != socketConfig.getVersion()) {
//                changeVersion(getChannel(), version);
//            }
//            if (socketConfig.getVersion() == CmppConstants.VERSION_20 && version >= CmppConstants.VERSION_30) {
//                changeVersion(getChannel(), CmppConstants.VERSION_30);
//            }
//            if (socketConfig.getVersion() == CmppConstants.VERSION_30 && version < CmppConstants.VERSION_30) {
//                changeVersion(getChannel(), CmppConstants.VERSION_20);
//            }
            //增加业务处理的handler
            addBIZHandler();
            //登录成功后的处理
            doAfterLogin();
            return true;
        } else {

            logger.info("{} login failed (status = {}) on channel {}", socketConfig.getId(), status, getChannel());
            //登录失败关闭channel
            close();
            setState(STATE_INITIAL);
        }
        return false;
    }

    /**
     * 根据版本号修改编解码handler
     *
     * @param channel
     * @param version
     */
    private void changeVersion(Channel channel, short version) {
        logger.debug("change to cmpp version {}", version);
        if (version < CmppConstants.VERSION_30) {
            channel.pipeline().replace("CmppMessageCodec", "Cmpp20MessageCodec", Cmpp20MessageCodec.getInstance());
        } else {
            channel.pipeline().replace("CmppMessageCodec", "Cmpp30MessageCodec", Cmpp30MessageCodec.getInstance());
        }

    }

    @Override
    public void fireExceptionThrown(Throwable t) {

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
        if (counters == null) {
            return;
        }
        AbstractCmppMessage msg = (AbstractCmppMessage) message;
        counters.countRXMessage(msg);
    }

    @Override
    protected boolean needSendLater(IMessage request, IMessage response) {
        if (response instanceof CmppSubmitResponseMessage) {
            CmppSubmitResponseMessage submitResp = (CmppSubmitResponseMessage) response;

            if ((submitResp.getResult() != 0L) && (submitResp.getResult() != OVER_SPEED_CODE)) {
                logger.error("Receive Err Response result: {} . Req: {} ,Resp:{}",submitResp.getResult(), request, submitResp);
            }

            return submitResp.getResult() == OVER_SPEED_CODE;
        }
        return false;
    }

}
