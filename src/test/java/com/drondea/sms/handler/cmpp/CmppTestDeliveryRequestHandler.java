package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.message.cmpp.CmppDeliverResponseMessage;
import com.drondea.sms.message.cmpp.CmppReportRequestMessage;
import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 回执处理器
 * @author: 刘彦宁
 * @date: 2020年06月22日08:48
 **/
public class CmppTestDeliveryRequestHandler extends SimpleChannelInboundHandler<CmppDeliverRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(CmppTestDeliveryRequestHandler.class);

    private static AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmppDeliverRequestMessage msg) throws Exception {
        int sequenceId = msg.getHeader().getSequenceId();
//        logger.info("sequenceId: {}", sequenceId);
        boolean report = msg.isReport();
//        logger.info("isReport: {}", report);
        if (report) {
            CmppReportRequestMessage reportMessage = msg.getReportRequestMessage();
            MsgId msgId = reportMessage.getMsgId();
            String stat = reportMessage.getStat();
            logger.info("msgId: {}, stat: {}",msgId, stat);
        } else {
            //是否长短信组装完毕
            boolean msgComplete = msg.isMsgComplete();
            if (msgComplete) {
                logger.debug("组装完毕，短信内容：{}", msg);

                //组装完成测试长短信拆分
//                ctx.channel().writeAndFlush(msg);
            } else {
                logger.debug("组装未完成，短信内容：{}", msg.getMsgContent());
            }
        }

        int incrementAndGet = atomicInteger.incrementAndGet();
//        new Thread(() -> {
//            try {
//                Thread.sleep(60 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (incrementAndGet == 2) {
//                try {
//                    Thread.sleep(180 * 1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("发送响应" + incrementAndGet);
//            ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
//            CmppDeliverResponseMessage response = new CmppDeliverResponseMessage(msg.getHeader());
//            response.setMsgId(new MsgId(channelSession.getSequenceNumber()));
//            response.setResult(0);
//            ctx.channel().writeAndFlush(response);
//        }).start();

        //发送响应
        CmppDeliverResponseMessage deliverResponseMessage = new CmppDeliverResponseMessage(msg.getHeader());
        deliverResponseMessage.setMsgId(msg.getMsgId());
        deliverResponseMessage.setResult(0);
        ctx.channel().writeAndFlush(deliverResponseMessage);

//      ctx.channel().writeAndFlush(msg);
    }
}
