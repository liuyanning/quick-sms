
package com.drondea.sms.handler.cmpp;

import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试收到的短信回应
 *
 * @author 27581
 */
@Sharable
public class CmppTestSubmitResponseHandler extends SimpleChannelInboundHandler<CmppSubmitResponseMessage> {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(CmppTestSubmitResponseHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, CmppSubmitResponseMessage msg) throws Exception {

//		System.out.println("CmppSubmitResponseMessage :" + msg.getSequenceId() + " result：" + msg.getResult());
        logger.debug("CmppSubmitResponseMessage : {} : result: {} MsgId: {}", msg.getSequenceId(), msg.getResult(), msg.getMsgId());
//        int i = atomicInteger.incrementAndGet();
//        if (i % 1000 == 0) {
//            System.out.println("测试：" + System.currentTimeMillis());
//        }
    }

}
