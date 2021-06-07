
package com.drondea.sms.handler.smgp;

import com.drondea.sms.message.smgp30.msg.SmgpSubmitResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 测试收到的短信回应
 *
 * @author 27581
 */
@Sharable
public class SmgpTestSubmitResponseHandler extends SimpleChannelInboundHandler<SmgpSubmitResponseMessage> {


    private static final Logger logger = LoggerFactory.getLogger(SmgpTestSubmitResponseHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SmgpSubmitResponseMessage msg) throws Exception {
//		System.out.println("SmgpSubmitResponseMessage :" + msg.getSequenceId() + " result：" + msg.getResult());
        logger.info("SmgpSubmitResponseMessage SequenceId: {} : Status: {} MsgId: {}", msg.getSequenceId(), msg.getStatus(), msg.getSmgpMsgId());

    }

}
