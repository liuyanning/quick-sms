package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.smpp34.SmppEnquireLinkRequestMessage;
import com.drondea.sms.message.smpp34.SmppEnquireLinkResponseMessage;
import com.drondea.sms.type.CmppConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 心跳请求包处理
 * @author: gengjinbiao
 * @date: 2020年07月15日11:49
 **/
@ChannelHandler.Sharable
public class SmppEnquireLinkRequestMessageHandler extends SimpleChannelInboundHandler<SmppEnquireLinkRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SmppEnquireLinkRequestMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmppEnquireLinkRequestMessage msg) {
        logger.debug("收到心跳检测请求，发送回应");
        SmppEnquireLinkResponseMessage resp = new SmppEnquireLinkResponseMessage();
        resp.getHeader().setSequenceNumber(msg.getHeader().getSequenceNumber());
        Channel channel = ctx.channel();
        ChannelSession channelSession = channel.attr(CmppConstants.NETTY_SESSION_KEY).get();
        //发送心跳检测回应数据
        channelSession.sendMessage(resp);
    }
}
