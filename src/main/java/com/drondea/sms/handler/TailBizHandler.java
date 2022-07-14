package com.drondea.sms.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 最后的handler
 * @author: 刘彦宁
 * @date: 2021年04月14日17:52
 **/
@ChannelHandler.Sharable
public class TailBizHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(TailBizHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.safeRelease(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("BlackHoleHandler exceptionCaught on channel {}", ctx.channel(), cause);
    }
}
