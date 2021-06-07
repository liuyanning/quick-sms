/**
 *
 */
package com.drondea.sms.handler.sgip;

import com.drondea.sms.message.sgip12.SgipUnbindResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 断开连接响应处理
 * @author liyuehai
 */
@Sharable
public class SgipUnbindResponseMessageHandler extends SimpleChannelInboundHandler<SgipUnbindResponseMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SgipUnbindResponseMessage e) throws Exception {
        ctx.channel().close();
    }

}
