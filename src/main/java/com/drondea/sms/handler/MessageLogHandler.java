package com.drondea.sms.handler;

import com.drondea.sms.message.IMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 最后的handler
 * @author: 刘彦宁
 * @date: 2021年04月14日17:52
 **/
@ChannelHandler.Sharable
public class MessageLogHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageLogHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!isActiveTest(msg)) {
            logger.debug(msg.toString());
        }
        super.channelRead(ctx, msg);
    }

    private boolean isActiveTest(Object msg) {
        if (!(msg instanceof IMessage)) {
            return false;
        }
        IMessage message = (IMessage) msg;
        return message.isActiveTestMessage();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!isActiveTest(msg)) {
            logger.debug(msg.toString());
        }
        super.write(ctx, msg, promise);
    }
}
