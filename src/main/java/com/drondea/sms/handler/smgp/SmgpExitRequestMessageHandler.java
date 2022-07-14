
package com.drondea.sms.handler.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.smgp30.msg.SmgpExitRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpExitResponseMessage;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 发起断开连接的请求
 *
 * @author 27581
 */
@Sharable
public class SmgpExitRequestMessageHandler extends SimpleChannelInboundHandler<SmgpExitRequestMessage> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SmgpExitRequestMessage e) throws Exception {
        SmgpExitResponseMessage responseMessage = new SmgpExitResponseMessage();
        responseMessage.getHeader().setSequenceId(e.getSequenceId());
        Channel channel = ctx.channel();
        ChannelSession channelSession = CommonUtil.getChannelSession(channel);
        //断开连接
        ChannelFuture future = channelSession.sendMessage(responseMessage);
        final ChannelHandlerContext finalctx = ctx;
        future.addListeners((GenericFutureListener) future1 -> ctx.executor().schedule(() -> {
            finalctx.channel().close();
        }, 500, TimeUnit.MILLISECONDS));

    }

}
