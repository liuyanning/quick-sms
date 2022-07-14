/**
 *
 */
package com.drondea.sms.handler.smgp;

import com.drondea.sms.message.smgp30.msg.SmgpExitResponseMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 断开连接响应处理
 * @author liuyanning
 */
@Sharable
public class SmgpTerminateResponseMessageHandler extends SimpleChannelInboundHandler<SmgpExitResponseMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SmgpExitResponseMessage e) throws Exception {
        ctx.channel().close();
    }

}
