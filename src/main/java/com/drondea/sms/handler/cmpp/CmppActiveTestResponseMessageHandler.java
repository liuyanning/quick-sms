package com.drondea.sms.handler.cmpp;

import com.drondea.sms.message.cmpp.CmppActiveTestResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 心跳检测请求处理
 * @author: 刘彦宁
 * @date: 2020年06月11日11:47
 **/
@ChannelHandler.Sharable
public class CmppActiveTestResponseMessageHandler extends SimpleChannelInboundHandler<CmppActiveTestResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(CmppActiveTestResponseMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmppActiveTestResponseMessage msg) throws Exception {
        logger.debug("收到心跳检测响应");
    }
}
