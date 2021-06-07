
package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.smpp34.SmppUnBindRequestMessage;
import com.drondea.sms.message.smpp34.SmppUnBindResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * 发起断开连接的请求
 *
 * @author 27581
 */
@Sharable
public class SmppTerminateRequestMessageHandler extends SimpleChannelInboundHandler<SmppUnBindRequestMessage> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SmppUnBindRequestMessage e) throws Exception {
        SmppUnBindResponseMessage responseMessage = new SmppUnBindResponseMessage(e.getHeader());
        Channel channel = ctx.channel();
        ChannelSession channelSession = CommonUtil.getChannelSession(channel);
        ChannelFuture future = channelSession.sendMessage(responseMessage);
        final ChannelHandlerContext finalctx = ctx;
        future.addListeners((GenericFutureListener) future1 -> ctx.executor().schedule(() -> {
            finalctx.channel().close();
        }, 500, TimeUnit.MILLISECONDS));

    }

}
