package com.drondea.sms.handler.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.smgp30.msg.SmgpActiveTestRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpActiveTestResponseMessage;
import com.drondea.sms.type.SmgpConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @version V3.0
 * @description: smgp心跳请求包处理
 * @author: ywj
 * @date: 2020年06月11日11:49
 **/
@ChannelHandler.Sharable
public class SmgpActiveTestRequestMessageHandler extends SimpleChannelInboundHandler<SmgpActiveTestRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SmgpActiveTestRequestMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmgpActiveTestRequestMessage msg) {
        logger.debug("收到心跳检测请求，发送回应");
        SmgpActiveTestResponseMessage resp = new SmgpActiveTestResponseMessage();
        resp.getHeader().setSequenceId(msg.getSequenceId());
        Channel channel = ctx.channel();
        ChannelSession channelSession = channel.attr(SmgpConstants.NETTY_SESSION_KEY).get();
        //发送心跳检测回应数据
        channelSession.sendMessage(resp);
    }
}
