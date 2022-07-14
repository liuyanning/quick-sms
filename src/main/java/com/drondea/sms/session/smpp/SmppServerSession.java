package com.drondea.sms.session.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.*;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: smpp的服务器端处理，接受客户端连接，鉴权等
 * @author: gengjinbiao
 * @date: 2020年07月15日15:39
 **/
public class SmppServerSession extends AbstractServerSession {

    private static final Logger logger = LoggerFactory.getLogger(SmppServerSession.class);

    /**
     * 创建session管理器
     *
     * @param sessionManager
     */
    public SmppServerSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        super(ctx, sessionManager);
        setSessionType("SMPP");
    }

    @Override
    public void dealConnectRequest(IMessage message) {
        //如果是登录信息
        if (!(message instanceof SmppBindTransceiverRequestMessage)) {
            return;
        }

        SmppBindTransceiverRequestMessage msg = (SmppBindTransceiverRequestMessage) message;

        String systemId = msg.getSystemId();

        AbstractServerSessionManager sessionManager = (AbstractServerSessionManager) getSessionManager();
        UserChannelConfig userChannelConfig = sessionManager.getUserChannelConfig(systemId);
        short version = msg.getInterfaceVersion();
        if (userChannelConfig == null) {
            //登录失败
            sendLoginFailed(msg, 15);
            return;
        }
        setUserName(userChannelConfig.getUserName());
        if (!validIpAddress(userChannelConfig, getChannel())) {
            failedLogin(userChannelConfig, msg, 10);
            //登录失败
            sendLoginFailed(msg, 10);
            return;
        }

        ICustomHandler customHandler = sessionManager.getCustomHandler();
        boolean customValidResult = true;
        if (customHandler != null) {
            customValidResult = customHandler.customLoginValidate(msg, userChannelConfig, getChannel());
        }

        int validResult = validClientMsg(msg.getPassword(), userChannelConfig);

        logger.debug("登录结果 {}, {}", validResult, customValidResult);
        //验证成功
        if (customValidResult && validResult == 0) {
            //添加session说明超过最大的连接限制数
            boolean addResult = sessionManager.addUserSession(userChannelConfig, this);
            logger.debug("校验成功,添加用户, {}", addResult);
            if (addResult) {
                setState(STATE_LOGIN_SUCCESS);
                sendLoginSuccess(msg, userChannelConfig, version);

                //登录成功后的处理
                doAfterLogin(userChannelConfig);

                //增加业务处理
                addBIZHandler();
                return;
            } else {
                //超过限制了失败，返回绑定失败
                validResult = 13;
            }

        } else {
            //密码错误
            validResult = 14;
        }

        failedLogin(userChannelConfig, msg, validResult);
        //登录失败
        sendLoginFailed(msg, validResult);
    }

    private void sendLoginSuccess(SmppBindTransceiverRequestMessage msg, UserChannelConfig userChannelConfig, short version) {
        SmppBindTransceiverResponseMessage responseMessage = createConnectResponseMsg(msg, SmppConstants.STATUS_OK);
        sendMessage(responseMessage);
    }

    private SmppBindTransceiverResponseMessage createConnectResponseMsg(SmppBindTransceiverRequestMessage msg, int status) {
        SmppBindTransceiverResponseMessage responseMessage = new SmppBindTransceiverResponseMessage();
        responseMessage.getHeader().setCommandStatus(status);
        responseMessage.getHeader().setSequenceNumber(msg.getHeader().getSequenceNumber());
        responseMessage.setSystemId(msg.getSystemId());
        return responseMessage;
    }

    private void sendLoginFailed(SmppBindTransceiverRequestMessage msg, int status) {
        SmppBindTransceiverResponseMessage responseMessage = createConnectResponseMsg(msg, status);
        ChannelFuture promise = sendMessage(responseMessage);
        final ChannelSession session = this;
        //关闭channel
        promise.addListener(future -> session.close());
    }

    private int validClientMsg(String password, UserChannelConfig userChannelConfig) {
        String passwordCheck = userChannelConfig.getPassword();

        if (passwordCheck.equals(password)) {
            return 0;
        } else {
            logger.error("AuthenticatorSource valided failed.s:{},c:{}", password, passwordCheck);
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
        channel.attr(SmppConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("ServerSessionFilterHandler", SmppConstants.SERVER_SESSION_FILTER_HANDLER);

        UserChannelConfig userChannelConfig = getUserChannelConfig();
        int idleTime = userChannelConfig.getIdleTime() == 0 ? 30 : userChannelConfig.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SmppIdleStateHandler", SmppConstants.IDLE_STATE_HANDLER);

        //长短信解析处理
        pipeline.addLast("SubmitLongMessageHandler", SmppConstants.SUBMIT_LONG_MESSAGE_HANDLER);

        pipeline.addLast("DeliverLongMessageHandler", SmppConstants.DELIVER_LONG_MESSAGE_HANDLER);

        SessionManager sessionManager = getSessionManager();


        AbstractServerSessionManager serverSessionManager = (AbstractServerSessionManager) sessionManager;
        AbstractCounterLimitHandler counterLimitHandler = serverSessionManager.getCounterLimitHandler(getUserName());
        if (counterLimitHandler != null) {
            pipeline.addLast("ServerCounterLimitHandler", counterLimitHandler);
            pipeline.addLast("ServerMetricsMeterHandler", GlobalConstants.SERVER_METRICS_METER_HANDLER);
        }

        pipeline.addLast("SmppEnquireLinkRequestMessageHandler", SmppConstants.ENQUIRE_LINK_REQUEST_HANDLER);
        pipeline.addLast("SmppEnquireLinkResponseMessageHandler", SmppConstants.ENQUIRE_LINK_RESPONSE_HANDLER);
//        pipeline.addLast("TerminateRequestHandler", CmppConstants.TERMINATE_REQUEST_MESSAGE_HANDLER);
//        pipeline.addLast("TerminateResponseHandler", CmppConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("SmppUserEventHandler", SmppConstants.SMPP_USER_EVENT_HANDLER);

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
     * @param
     * @param
     */
//    private void changeVersion(Channel channel, short version) {
//        logger.debug("change to cmpp version {}", version);
//        if (version == CmppConstants.VERSION_20) {
//            channel.pipeline().replace("CmppMessageCodec","Cmpp20MessageCodec", Cmpp20MessageCodec.getInstance());
//        } else {
//            channel.pipeline().replace("CmppMessageCodec","Cmpp30MessageCodec", Cmpp30MessageCodec.getInstance());
//        }
//
//    }
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
    public void fireExceptionThrown(Throwable t) {
        if (t.getCause() instanceof InvalidCommandIdException) {
            logger.error("smpp InvalidCommandIdException:", t);
            InvalidMessageException imException = (InvalidMessageException) t;
            SmppHeader header = (SmppHeader) imException.getMsg();
            SmppGenericNackMessage message = new SmppGenericNackMessage();
            message.getHeader().setCommandId(SmppConstants.CMD_ID_GENERIC_NACK);
            message.getHeader().setCommandStatus(SmppConstants.STATUS_INVSYSID);
            message.getHeader().setSequenceNumber(header.getSequenceNumber());
            sendMessage(message);
        }
        if (t.getCause() instanceof InvalidMessageException) {
            logger.error("smpp InvalidCommandIdException:", t);
            InvalidMessageException imException = (InvalidMessageException) t;
            AbstractSmppMessage exceptionMsg = (AbstractSmppMessage) imException.getMsg();
            SmppGenericNackMessage message = new SmppGenericNackMessage();
            message.getHeader().setCommandId(SmppConstants.CMD_ID_GENERIC_NACK);
            message.getHeader().setCommandStatus(SmppConstants.STATUS_INVCMDID);
            message.getHeader().setSequenceNumber(exceptionMsg.getHeader().getSequenceNumber());
            sendMessage(message);
        }
    }
    @Override
    protected boolean needSendLater(IMessage request, IMessage response) {
        return false;
    }
}
