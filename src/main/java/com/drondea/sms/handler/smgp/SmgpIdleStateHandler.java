package com.drondea.sms.handler.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.smgp30.msg.SmgpActiveTestRequestMessage;
import com.drondea.sms.type.SmgpConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @version V3.0
 * @description: 心跳检测超时事件处理器
 * @author: ywj
 * @date: 2020年06月10日09:31
 **/

@ChannelHandler.Sharable
public class SmgpIdleStateHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmgpIdleStateHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
                //如果通道active的话写入activeTest,如果不可写关闭
                if (ctx.channel().isActive()) {
                    SmgpActiveTestRequestMessage msg = new SmgpActiveTestRequestMessage();
                    ChannelSession channelSession = ctx.channel().attr(SmgpConstants.NETTY_SESSION_KEY).get();
                    msg.getHeader().setSequenceId(channelSession.getSequenceNumber().next());
                    ctx.channel().writeAndFlush(msg);
                } else {
                    logger.error("心跳超时关闭连接1 {}", e.state());
                    ctx.channel().close();
                }
            } else if (e.state() == IdleState.READER_IDLE) {
                logger.error("心跳超时关闭连接2 {}", e.state());
                //触发读超时事件，说明心跳好几次对方都没有响应，关闭
                ctx.channel().close();
            }
            logger.debug("触发了事件 {}", e.state());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
