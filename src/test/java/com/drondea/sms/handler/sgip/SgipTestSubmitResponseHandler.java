
package com.drondea.sms.handler.sgip;

import com.drondea.sms.message.sgip12.SgipSubmitResponseMessage;
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
public class SgipTestSubmitResponseHandler extends SimpleChannelInboundHandler<SgipSubmitResponseMessage> {


    private static final Logger logger = LoggerFactory.getLogger(SgipTestSubmitResponseHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SgipSubmitResponseMessage msg) throws Exception {

//		System.out.println("SgipSubmitResponseMessage :" + msg.getSequenceId() + " result：" + msg.getResult());
        logger.debug("SgipSubmitResponseMessage : {} : result: {}", msg.getSequenceId(), msg.getResult());

    }

}
