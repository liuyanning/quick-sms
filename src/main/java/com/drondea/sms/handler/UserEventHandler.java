package com.drondea.sms.handler;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 用户事件处理
 * @author: 刘彦宁
 * @date: 2020年06月23日17:13
 **/
@ChannelHandler.Sharable
public class UserEventHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserEventHandler.class);

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
