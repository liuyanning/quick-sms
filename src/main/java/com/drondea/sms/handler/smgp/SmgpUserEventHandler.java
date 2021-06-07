package com.drondea.sms.handler.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.ICustomHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0
 * @description: 用户事件处理
 * @author: ywj
 * @date: 2020年06月23日17:13
 **/
@ChannelHandler.Sharable
public class SmgpUserEventHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(SmgpUserEventHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == (Integer) ChannelSession.STATE_LOGIN_SUCCESS) {
            ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
            SessionManager sessionManager = channelSession.getSessionManager();
            ICustomHandler customHandler = sessionManager.getCustomHandler();
            if (customHandler == null) {
                logger.error("customHandler must be set.");
                return;
            }
            customHandler.fireUserLogin(ctx.channel(), channelSession);
        }
        super.userEventTriggered(ctx, evt);
    }
}
