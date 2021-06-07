package com.drondea.sms.handler.smgp;


import com.drondea.sms.message.smgp30.msg.SmgpActiveTestResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @version V3.0
 * @description: smgp心跳检测请求处理
 * @author: ywj
 * @date: 2020年06月11日11:47
 **/
@ChannelHandler.Sharable
public class SmgpActiveTestResponseMessageHandler extends SimpleChannelInboundHandler<SmgpActiveTestResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SmgpActiveTestResponseMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmgpActiveTestResponseMessage msg) throws Exception {
        logger.debug("收到心跳检测响应");
    }
}
