package com.drondea.sms.session.sgip;

import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.conf.sgip.SgipClientSocketConfig;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.AbstractSgipMessage;
import com.drondea.sms.message.sgip12.SgipBindRequestMessage;
import com.drondea.sms.message.sgip12.SgipBindResponseMessage;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.SgipConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: Sgip的session管理
 * @author: liyuehai
 * @date: 2020年06月05日18:34
 **/
public class SgipClientSession extends AbstractClientSession {

    private static final Logger logger = LoggerFactory.getLogger(SgipClientSession.class);

    /**
     * 创建session管理器
     *
     * @param ctx
     * @param connectorManager
     */
    public SgipClientSession(ChannelHandlerContext ctx, SessionManager connectorManager) {
        super(ctx, connectorManager);
    }

    @Override
    protected void sendLoginMsg() {
        SgipClientSocketConfig connConf = (SgipClientSocketConfig) getConfiguration();
        SgipBindRequestMessage requestMessage = new SgipBindRequestMessage();
        SgipSequenceNumber sequenceNumber = new SgipSequenceNumber(connConf.getNodeId(), getSequenceNumber().next());
        requestMessage.getHeader().setSequenceNumber(sequenceNumber);

        String userName = connConf.getUserName();
        requestMessage.setLoginName(userName);
        if (StringUtils.isEmpty(userName)) {
            logger.error("userName cannot be empty");
            return;
        }
        String password = connConf.getPassword();
        if (StringUtils.isEmpty(password)) {
            logger.error("password cannot be empty");
            return;
        }
        short loginType = connConf.getLoginType();
        //loginType登录类型
        // 1：SP向SMG建立的连接，用于发送命令
        // 2：SMG向SP建立的连接，用于发送命令
        if(loginType != 0){
            requestMessage.setLoginType(loginType);
        }
        requestMessage.setLoginPassowrd(password);
        Charset charset = connConf.getCharset();
        if (charset == null) {
            charset = SgipConstants.DEFAULT_TRANSPORT_CHARSET;
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
        channel.attr(SgipConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        SgipClientSocketConfig connConf = (SgipClientSocketConfig) getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 30 : connConf.getIdleTime();
        //在idleTime秒没有读写就关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(0, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SgipIdleStateHandler", SgipConstants.IDLE_STATE_HANDLER);
        //长短信解析处理
        pipeline.addLast("SubmitLongMessageHandler", SgipConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", SgipConstants.DELIVER_LONG_MESSAGE_HANDLER);

        pipeline.addLast("UnbindRequestHandler", SgipConstants.UNBIND_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("UnbindResponseHandler", SgipConstants.UNBIND_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("SgipUserEventHandler", SgipConstants.SGIP_USER_EVENT_HANDLER);

        //增加自定义的handler
        SessionManager sessionManager = getSessionManager();
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.configPipelineAfterLogin(pipeline);
        }

        //发起一个事件，用户登录成功，可以发送短信了，要放在后边才能通知前边所有handler
        notifyChannelLoginSuccess(channel);

    }


    @Override
    public boolean dealConnectResponseMessage(IMessage message) {
        //如果是登录响应信息
        if (!(message instanceof SgipBindResponseMessage)) {
            logger.error("用户登录却返回了:" + message);
            return false;
        }
        SgipBindResponseMessage msg = (SgipBindResponseMessage) message;
        SgipClientSocketConfig socketConfig = (SgipClientSocketConfig) getConfiguration();
        int status = msg.getResult();
        boolean customLoginValid = customLoginValid(message);
        if (status == 0 && customLoginValid) {
            logger.info("{} login success", socketConfig.getId());
            setState(STATE_LOGIN_SUCCESS);
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
    protected boolean needSendLater(IMessage request, IMessage response) {
        return false;
    }
}
