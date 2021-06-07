/**
 *
 */
package com.drondea.sms.handler.smpp;

import com.drondea.sms.message.smpp34.SmppUnBindResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 断开连接响应处理
 * @author gengjinbiao
 */
@Sharable
public class SmppTerminateResponseMessageHandler extends SimpleChannelInboundHandler<SmppUnBindResponseMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SmppUnBindResponseMessage e) throws Exception {
        ctx.channel().close();
    }

}
