package com.drondea.sms.session.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.AbstractSgipMessage;
import com.drondea.sms.message.sgip12.SgipBindRequestMessage;
import com.drondea.sms.message.sgip12.SgipBindResponseMessage;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.SgipConstants;
import com.drondea.sms.type.UserChannelConfig;
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
 * @description: Sgip的服务器端处理，接受客户端连接，鉴权等
 * @author: liyuehai
 * @date: 2020年06月10日15:39
 **/
public class SgipServerSession extends AbstractServerSession {

    private static final Logger logger = LoggerFactory.getLogger(SgipServerSession.class);

    /**
     * 创建session管理器
     *
     * @param sessionManager
     */
    public SgipServerSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        super(ctx, sessionManager);
        setSessionType("SGIP");
    }

    @Override
    public void dealConnectRequest(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof SgipBindRequestMessage)) {
            return;
        }

        SgipBindRequestMessage msg = (SgipBindRequestMessage) message;
        String userName = msg.getLoginName();
        AbstractServerSessionManager sessionManager = (AbstractServerSessionManager) getSessionManager();
        UserChannelConfig userChannelConfig = sessionManager.getUserChannelConfig(userName);
        if (userChannelConfig == null) {
            //登录失败
            sendLoginFailed(msg, null, 6);
            return;
        }
        setUserName(userChannelConfig.getUserName());
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        boolean customValidResult = true;
        if (customHandler != null) {
            customValidResult = customHandler.customLoginValidate(msg, userChannelConfig, getChannel());
        }

        int validResult = validClientMsg(msg.getLoginPassowrd(), userChannelConfig);
        boolean isValidIp = validIpAddress(userChannelConfig, getChannel());
        logger.debug("登录结果 {}, {}", validResult, customValidResult);
        //验证成功
        if (customValidResult && validResult == 0 && isValidIp) {
            //添加session说明超过最大的连接限制数
            boolean addResult = sessionManager.addUserSession(userChannelConfig, this);
            if (addResult) {
                setState(STATE_LOGIN_SUCCESS);
                sendLoginSuccess(msg, userChannelConfig);

                //登录成功后的处理，不开启拉取消息的定时，（SGIP的Server只负责收消息）
                doAfterLogin(userChannelConfig, false);

                //增加业务处理
                addBIZHandler();
                return;
            } else {
                //超过限制了失败
                validResult = 5;
            }

        } else {
            validResult = 3;
        }
        //IP不合法错误码4
        if (!isValidIp) {
            validResult = 2;
        }
        failedLogin(userChannelConfig, msg, validResult);
        //登录失败
        sendLoginFailed(msg, userChannelConfig, validResult);
    }

    private void sendLoginSuccess(SgipBindRequestMessage msg, UserChannelConfig userChannelConfig) {
        SgipBindResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, 0);
        sendMessage(responseMessage);
    }

    private SgipBindResponseMessage createConnectResponseMsg(SgipBindRequestMessage msg, UserChannelConfig userChannelConfig, int status) {
        SgipBindResponseMessage responseMessage = new SgipBindResponseMessage();
        responseMessage.getHeader().setSequenceNumber(msg.getHeader().getSequenceNumber());
        responseMessage.setResult((short) status);
        return responseMessage;
    }

    private void sendLoginFailed(SgipBindRequestMessage msg, UserChannelConfig userChannelConfig,
                                 int status) {
        SgipBindResponseMessage responseMessage = createConnectResponseMsg(msg, userChannelConfig, status);
        ChannelFuture promise = sendMessage(responseMessage);
        final ChannelSession session = this;
        //关闭channel
        promise.addListener(future -> session.close());
    }

    private int validClientMsg(String password, UserChannelConfig userChannelConfig) {
        String passwordCheck = userChannelConfig.getPassword();
        if (password.equals(passwordCheck)) {
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
        channel.attr(SgipConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("ServerSessionFilterHandler", SgipConstants.SERVER_SESSION_FILTER_HANDLER);

        ServerSocketConfig connConf = getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 60 : connConf.getIdleTime();
        //在idleTime秒没有读写就关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(0, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SgipIdleStateHandler", SgipConstants.IDLE_STATE_HANDLER);
        //长短信解析处理
        pipeline.addLast("SubmitLongMessageHandler", SgipConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", SgipConstants.DELIVER_LONG_MESSAGE_HANDLER);

        SessionManager sessionManager = getSessionManager();

        AbstractServerSessionManager serverSessionManager = (AbstractServerSessionManager) sessionManager;
        AbstractCounterLimitHandler counterLimitHandler = serverSessionManager.getCounterLimitHandler(getUserName());
        if (counterLimitHandler != null) {
            pipeline.addLast("ServerCounterLimitHandler", counterLimitHandler);
            pipeline.addLast("ServerMetricsMeterHandler", GlobalConstants.SERVER_METRICS_METER_HANDLER);
        }

        pipeline.addLast("UnbindRequestHandler", SgipConstants.UNBIND_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("UnbindResponseHandler", SgipConstants.UNBIND_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("SgipUserEventHandler", SgipConstants.SGIP_USER_EVENT_HANDLER);

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
        SgipSessionCounters counters = (SgipSessionCounters) getCounters();
        AbstractSgipMessage msg = (AbstractSgipMessage) message;
        counters.countTXMessage(msg);
    }

    @Override
    public void countRXMessage(IMessage message) {
        SgipSessionCounters counters = (SgipSessionCounters) getCounters();
        AbstractSgipMessage msg = (AbstractSgipMessage) message;
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
