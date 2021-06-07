package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.cmpp.CmppActiveTestRequestMessage;
import com.drondea.sms.message.cmpp.CmppActiveTestResponseMessage;
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
 * @author: 刘彦宁
 * @date: 2020年06月11日11:49
 **/
@ChannelHandler.Sharable
public class CmppActiveTestRequestMessageHandler extends SimpleChannelInboundHandler<CmppActiveTestRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(CmppActiveTestRequestMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmppActiveTestRequestMessage msg) {
        logger.debug("收到心跳检测请求，发送回应");
        CmppActiveTestResponseMessage resp = new CmppActiveTestResponseMessage();
        resp.getHeader().setSequenceId(msg.getHeader().getSequenceId());
        Channel channel = ctx.channel();
        ChannelSession channelSession = channel.attr(CmppConstants.NETTY_SESSION_KEY).get();
        //发送心跳检测回应数据
        channelSession.sendMessage(resp);
    }
}
