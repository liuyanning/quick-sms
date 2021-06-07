/**
 *
 */
package com.drondea.sms.handler.cmpp;

import com.drondea.sms.message.cmpp.CmppTerminateResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 断开连接响应处理
 * @author liuyanning
 */
@Sharable
public class CmppTerminateResponseMessageHandler extends SimpleChannelInboundHandler<CmppTerminateResponseMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, CmppTerminateResponseMessage e) throws Exception {
        ctx.channel().close();
    }

}
