package com.drondea.sms.handler.smpp;

import com.drondea.sms.message.smpp34.SmppEnquireLinkResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 心跳检测响应处理
 * @author: gengjinbiao
 * @date: 2020年07月15日11:47
 **/
@ChannelHandler.Sharable
public class SmppEnquireLinkResponseMessageHandler extends SimpleChannelInboundHandler<SmppEnquireLinkResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SmppEnquireLinkResponseMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmppEnquireLinkResponseMessage msg) throws Exception {
        logger.debug("收到心跳检测响应");
    }
}
