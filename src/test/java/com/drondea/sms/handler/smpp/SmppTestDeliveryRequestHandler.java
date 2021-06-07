package com.drondea.sms.handler.smpp;

import com.drondea.sms.handler.cmpp.ServerCmppSubmitRequestHandler;
import com.drondea.sms.message.smpp34.SmppDeliverSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppDeliverSmResponseMessage;
import com.drondea.sms.message.smpp34.SmppReportRequestMessage;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 回执处理器
 * @author: gengjinbiao
 * @date: 2020年06月22日08:48
 **/
public class SmppTestDeliveryRequestHandler extends SimpleChannelInboundHandler<SmppDeliverSmRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ServerCmppSubmitRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmppDeliverSmRequestMessage msg) throws Exception {
        int sequenceId = msg.getHeader().getSequenceNumber();
//        logger.debug("sequenceId: {}", sequenceId);
        boolean report = msg.isReport();
//        logger.debug("smpp isReport: {}", report);
        if (report) {
            SmppReportRequestMessage reportRequest = msg.getReportRequest();
            String msgId = reportRequest.getId();
            String stat = reportRequest.getStat();
            logger.debug("msgId: {}, stat: {}", msgId, stat);
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
        SmppDeliverSmResponseMessage deliverResponseMessage = new SmppDeliverSmResponseMessage(msg.getHeader());
        deliverResponseMessage.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
        ctx.channel().writeAndFlush(deliverResponseMessage);

//      ctx.channel().writeAndFlush(msg);
    }
}
