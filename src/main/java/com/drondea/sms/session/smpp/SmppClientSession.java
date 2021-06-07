package com.drondea.sms.session.smpp;

import com.drondea.sms.conf.smpp.SmppClientSocketConfig;
import com.drondea.sms.handler.transcoder.Cmpp20MessageCodec;
import com.drondea.sms.handler.transcoder.Cmpp30MessageCodec;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.AbstractSmppMessage;
import com.drondea.sms.message.smpp34.SmppBindTransceiverRequestMessage;
import com.drondea.sms.message.smpp34.SmppBindTransceiverResponseMessage;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: smpp的session管理
 * @author: 刘彦宁
 * @date: 2020年06月05日18:34
 **/
public class SmppClientSession extends AbstractClientSession {

    private static final Logger logger = LoggerFactory.getLogger(SmppClientSession.class);

    /**
     * 创建session管理器
     *
     * @param ctx
     * @param connectorManager
     */
    public SmppClientSession(ChannelHandlerContext ctx, SessionManager connectorManager) {
        super(ctx, connectorManager);
    }

    //todo 登录
    @Override
    protected void sendLoginMsg() {
        SmppClientSocketConfig connConf = (SmppClientSocketConfig) getConfiguration();
        SmppBindTransceiverRequestMessage bindMessage = new SmppBindTransceiverRequestMessage();
        bindMessage.getHeader().setSequenceNumber(getSequenceNumber().next());
        String systemId = connConf.getSystemId();
        if (StringUtils.isEmpty(systemId)) {
            logger.error("systemId cannot be empty");
            return;
        }
        bindMessage.setSystemId(systemId);
        String password = connConf.getPassword();
        if (StringUtils.isEmpty(password)) {
            logger.error("password cannot be empty");
            return;
        }
        bindMessage.setPassword(password);
        bindMessage.setSystemType(connConf.getSystemType());
        bindMessage.setInterfaceVersion(connConf.getVersion());

        setState(STATE_LOGINING);
        //发送登录数据
        sendMessage(bindMessage);
    }

    /**
     * 添加业务处理的handler
     */
    private void addBIZHandler() {
        logger.debug("添加业务处理器");
        Channel channel = getChannel();
        //channel绑定session对象
        channel.attr(SmppConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        SmppClientSocketConfig connConf = (SmppClientSocketConfig) getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 40 : connConf.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SmppIdleStateHandler", SmppConstants.IDLE_STATE_HANDLER);
//
        pipeline.addLast("SubmitLongMessageHandler", SmppConstants.SUBMIT_LONG_MESSAGE_HANDLER);

        pipeline.addLast("DeliverLongMessageHandler", SmppConstants.DELIVER_LONG_MESSAGE_HANDLER);
//
        pipeline.addLast("SmppEnquireLinkRequestMessageHandler", SmppConstants.ENQUIRE_LINK_REQUEST_HANDLER);
        pipeline.addLast("SmppEnquireLinkResponseMessageHandler", SmppConstants.ENQUIRE_LINK_RESPONSE_HANDLER);

        //端口连接处理
        pipeline.addLast("TerminateRequestHandler", SmppConstants.TERMINATE_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("TerminateResponseHandler", SmppConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);
//
//        //用户事件处理器
        pipeline.addLast("SmppUserEventHandler", SmppConstants.SMPP_USER_EVENT_HANDLER);

        //增加自定义的handler
        SessionManager sessionManager = getSessionManager();
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.configPipelineAfterLogin(pipeline);
        }

        //发起一个事件，用户登录成功，可以发送短信了，要放在后边才能通知前边所有handler
        notifyChannelLoginSuccess(channel);

    }

    //todo 登录响应处理
    @Override
    public boolean dealConnectResponseMessage(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof SmppBindTransceiverResponseMessage)) {
            logger.error("用户登录却返回了:" + message);
            return false;
        }
        SmppBindTransceiverResponseMessage msg = (SmppBindTransceiverResponseMessage) message;
        SmppClientSocketConfig socketConfig = (SmppClientSocketConfig) getConfiguration();

        int status = msg.getHeader().getCommandStatus();

        boolean customLoginValid = customLoginValid(message);
        if (status == 0 && customLoginValid) {
            logger.info("{} login success", socketConfig.getId());
            setState(STATE_LOGIN_SUCCESS);
            //服务器返回的版本号不一致，适应服务器端
//            if (version != socketConfig.getVersion()) {
//                changeVersion(getChannel(), version);
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

    @Override
    public void fireExceptionThrown(Throwable t) {

    }

    @Override
    public void countTXMessage(IMessage message) {
        SmppSessionCounters counters = (SmppSessionCounters) getCounters();
        AbstractSmppMessage msg = (AbstractSmppMessage) message;
        counters.countTXMessage(msg);
    }

    @Override
    public void countRXMessage(IMessage message) {
        SmppSessionCounters counters = (SmppSessionCounters) getCounters();
        AbstractSmppMessage msg = (AbstractSmppMessage) message;
        counters.countRXMessage(msg);
    }

    @Override
    protected boolean needSendLater(IMessage request, IMessage response) {
        return false;
    }
}
