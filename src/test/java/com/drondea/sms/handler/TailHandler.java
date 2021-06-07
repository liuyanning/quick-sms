package com.drondea.sms.handler;

import com.drondea.sms.handler.sgip.SgipClientCustomHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 最后的handler
 * @author: 刘彦宁
 * @date: 2021年04月14日17:52
 **/
public class TailHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(TailHandler.class);
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exception handler", cause);
        super.exceptionCaught(ctx, cause);
    }
}
