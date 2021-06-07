package com.drondea.sms.handler.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.sgip12.SgipUnbindRequestMessage;
import com.drondea.sms.type.CmppConstants;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 超时事件处理器，用于客户端命令发送完并接收到所有应答后，客户端应该主动断开连接
 * @author: liyuehai
 * @date: 2020年06月10日09:31
 **/

@ChannelHandler.Sharable
public class SgipIdleStateHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(SgipIdleStateHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
                //如果通道active的话写入activeTest,如果不可写关闭
                if (ctx.channel().isActive()) {
                    SgipUnbindRequestMessage msg = new SgipUnbindRequestMessage();
                    ChannelSession channelSession = ctx.channel().attr(CmppConstants.NETTY_SESSION_KEY).get();
                    msg.getHeader().setSequenceNumber(new SgipSequenceNumber(1, channelSession.getSequenceNumber().next()));
                    ctx.channel().writeAndFlush(msg);
                } else {
                    logger.error("心跳超时关闭连接1 {}", e.state());
                    ctx.channel().close();
                }
            }
            logger.debug("触发了事件 {}", e.state());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
