package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.message.smpp34.SmppBindTransceiverRequestMessage;
import com.drondea.sms.session.smpp.SmppServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 服务端的过滤器逻辑
 * @author: 刘彦宁
 * @date: 2020年06月11日13:49
 **/
@ChannelHandler.Sharable
public class SmppServerSessionFilterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SmppServerSessionFilterHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //不处理ConnectRequest
        if ((msg instanceof SmppBindTransceiverRequestMessage)) {
            return;
        }

        //如果没有登录过返回
        SmppServerSession channelSession = (SmppServerSession) CommonUtil.getChannelSession(ctx.channel());

        if (channelSession.getState() != ChannelSession.STATE_LOGIN_SUCCESS) {
            logger.error("尚未登录，不能处理此请求");
            //非法直接关掉
            channelSession.close();
            return;
        }

        AbstractServerSessionManager sessionManager = (AbstractServerSessionManager) channelSession.getSessionManager();
        CounterRateLimiter userQPSLimiter = sessionManager.getUserQPSLimiter(channelSession.getUserName());
        if (userQPSLimiter != null) {
            if (!userQPSLimiter.tryAcquire()) {
                //达到限速了
                //todo 限制用户的发送速度，达到限速怎么处理
                System.out.println("服务器端达到限速");
            }
        }

        super.channelRead(ctx, msg);

    }

}
