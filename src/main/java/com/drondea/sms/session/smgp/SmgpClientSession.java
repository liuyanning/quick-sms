package com.drondea.sms.session.smgp;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SystemClock;
import com.drondea.sms.conf.smgp.SmgpClientSocketConfig;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.AbstractSmgpMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectResponseMessage;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.SmgpConstants;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @version V3.0
 * @description: smgp的客户端session管理
 * @author: ywj
 * @date: 2020年06月05日18:34
 **/
public class SmgpClientSession extends AbstractClientSession {

    private static final Logger logger = LoggerFactory.getLogger(SmgpClientSession.class);

    /**
     * 创建session管理器
     *
     * @param ctx
     * @param connectorManager
     */
    public SmgpClientSession(ChannelHandlerContext ctx, SessionManager connectorManager) {
        super(ctx, connectorManager);
    }

    @Override
    protected void sendLoginMsg() {
        SmgpClientSocketConfig connConf = (SmgpClientSocketConfig) getConfiguration();
        SmgpConnectRequestMessage requestMessage = new SmgpConnectRequestMessage();
        requestMessage.getHeader().setSequenceId(getSequenceNumber().next());
        String userName = connConf.getUserName();
        if (StringUtils.isEmpty(userName)) {
            logger.error("userName cannot be empty");
            return;
        }
        requestMessage.setClientId(userName);
        String timestamp = DateFormatUtils.format(SystemClock.now(), "MMddHHmmss");
        requestMessage.setTimestamp(Long.parseLong(timestamp));
        String password = connConf.getPassword();
        if (StringUtils.isEmpty(password)) {
            logger.error("password cannot be empty");
            return;
        }
        Charset charset = connConf.getCharset();
        if (charset == null) {
            charset = SmgpConstants.DEFAULT_TRANSPORT_CHARSET;
        }
        requestMessage.setAuthenticatorClient(CommonUtil.getAuthenticatorSourceSmgp(userName, password, timestamp, charset));
        short version = connConf.getVersion();
        if (version == 0) {
            version = SmgpConstants.DEFAULT_VERSION;
        }
        requestMessage.setClientVersion((byte) version);
        requestMessage.setLoginMode((byte) 2);
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
        channel.attr(SmgpConstants.NETTY_SESSION_KEY).set(this);

        ChannelPipeline pipeline = channel.pipeline();

        SmgpClientSocketConfig connConf = (SmgpClientSocketConfig) getConfiguration();
        int idleTime = connConf.getIdleTime() == 0 ? 40 : connConf.getIdleTime();
        //心跳检测,在idleTime秒没有读写就写入activeTest请求，如果idleTime * 3服务器没有相应数据那么关掉连接
        pipeline.addLast("IdleStateHandler",
                new IdleStateHandler(idleTime * 3, 0, idleTime, TimeUnit.SECONDS));
        //超时处理
        pipeline.addLast("SmgpIdleStateHandler", SmgpConstants.IDLE_STATE_HANDLER);

        //长短信处理
        pipeline.addLast("SubmitLongMessageHandler", SmgpConstants.SUBMIT_LONG_MESSAGE_HANDLER);
        pipeline.addLast("DeliverLongMessageHandler", SmgpConstants.DELIVER_LONG_MESSAGE_HANDLER);

        pipeline.addLast("ActiveTestRequestHandler", SmgpConstants.ACTIVE_TEST_REQUEST_HANDLER);
        pipeline.addLast("ActiveTestResponseHandler", SmgpConstants.ACTIVE_TEST_RESPONSE_HANDLER);
        pipeline.addLast("TerminateRequestHandler", SmgpConstants.TERMINATE_REQUEST_MESSAGE_HANDLER);
        pipeline.addLast("TerminateResponseHandler", SmgpConstants.TERMINATE_RESPONSE_MESSAGE_HANDLER);

        //用户事件处理器
        pipeline.addLast("SmgpUserEventHandler", SmgpConstants.SMGP_USER_EVENT_HANDLER);

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
        if (!(message instanceof SmgpConnectResponseMessage)) {
            logger.error("用户登录却返回了:" + message);
            return false;
        }
        SmgpConnectResponseMessage msg = (SmgpConnectResponseMessage) message;
        SmgpClientSocketConfig socketConfig = (SmgpClientSocketConfig) getConfiguration();
        long status = msg.getStatus();
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
    protected boolean needSendLater(IMessage request, IMessage response) {
        return false;
    }

}
