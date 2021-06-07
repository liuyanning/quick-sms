package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.message.cmpp.CmppConnectRequestMessage;
import com.drondea.sms.session.cmpp.CmppServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 服务端的过滤器逻辑
 * @author: 刘彦宁
 * @date: 2020年06月11日13:49
 **/
@ChannelHandler.Sharable
public class CmppServerSessionFilterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CmppServerSessionFilterHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //不处理ConnectRequest
        if ((msg instanceof CmppConnectRequestMessage)) {
            return;
        }

        //如果没有登录过返回
        CmppServerSession channelSession = (CmppServerSession) CommonUtil.getChannelSession(ctx.channel());

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
                logger.debug("服务器端达到限速");
            }
        }

        super.channelRead(ctx, msg);

    }

}
