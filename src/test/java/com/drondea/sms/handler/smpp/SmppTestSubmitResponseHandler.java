
package com.drondea.sms.handler.smpp;

import com.drondea.sms.message.smpp34.SmppSubmitSmResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试收到的短信回应
 *
 * @author 27581
 */
@Sharable
public class SmppTestSubmitResponseHandler extends SimpleChannelInboundHandler<SmppSubmitSmResponseMessage> {


    private static final Logger logger = LoggerFactory.getLogger(SmppTestSubmitResponseHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SmppSubmitSmResponseMessage msg) throws Exception {

//		System.out.println("CmppSubmitResponseMessage :" + msg.getSequenceId() + " result：" + msg.getResult());
        logger.debug("SmppSubmitResponseMessage : {} : msgId: {}", msg.getHeader().toString(), msg.getMessageId());

    }

}
