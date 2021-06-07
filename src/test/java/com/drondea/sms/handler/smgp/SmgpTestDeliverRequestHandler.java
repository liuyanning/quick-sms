
package com.drondea.sms.handler.smgp;

import com.drondea.sms.message.smgp30.msg.SmgpDeliverRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpDeliverResponseMessage;
import com.drondea.sms.message.smgp30.msg.SmgpReportMessage;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试 DeliverResponse
 *
 * @author ywj
 */
@Sharable
public class SmgpTestDeliverRequestHandler extends SimpleChannelInboundHandler<SmgpDeliverRequestMessage> {


    private static final Logger logger = LoggerFactory.getLogger(SmgpTestDeliverRequestHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, SmgpDeliverRequestMessage msg) throws Exception {
        int sequenceId = msg.getHeader().getSequenceId();
        logger.info("sequenceId: {}", sequenceId);
        boolean report = msg.isReport();
        logger.info("smgp isReport: {}", report);
        if (report) {
            SmgpReportMessage reportRequest = msg.getReport();
            String msgId = reportRequest.getSmgpMsgId().toString();
            String stat = reportRequest.getStat();
            logger.info("msgId: {}, stat: {}", msgId, stat);
        } else {
            //是否长短信组装完毕
            boolean msgComplete = msg.isMsgComplete();
            if (msgComplete) {
                logger.debug("组装完毕，短信内容：{}", msg.getMsgContent());
            } else {
                logger.debug("组装未完成，短信内容：{}", msg.getMsgContent());
            }
        }

        //发送响应
        SmgpDeliverResponseMessage deliverResponseMessage = new SmgpDeliverResponseMessage(msg.getHeader());
        deliverResponseMessage.setSmgpMsgId(msg.getSmgpMsgId());
        deliverResponseMessage.setStatus(SmppConstants.STATUS_OK);
        ctx.channel().writeAndFlush(deliverResponseMessage);
    }

}
