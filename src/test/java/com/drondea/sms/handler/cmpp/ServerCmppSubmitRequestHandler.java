package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.message.cmpp.CmppReportRequestMessage;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 测试服务器接收短信数据
 * @author: 刘彦宁
 * @date: 2020年06月18日17:26
 **/
public class ServerCmppSubmitRequestHandler extends SimpleChannelInboundHandler<CmppSubmitRequestMessage> {

    private static AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerCmppSubmitRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmppSubmitRequestMessage msg) throws Exception {

        //是否长短信组装完毕
        boolean msgComplete = msg.isMsgComplete();
        if (msgComplete) {
            logger.debug("组装完毕，总共 {} 条，第 {} 条，短信内容：{}, 批次号：{}", msg.getPkTotal(), msg.getPkNumber(),
                    msg.getMsgContent(), msg.getBatchNumber());
        } else {
            logger.debug("组装未完成，总共 {} 条，第 {} 条，短信内容：{}，批次号：{}", msg.getPkTotal(), msg.getPkNumber(),
                    msg.getMsgContent(), msg.getBatchNumber());
        }
        int incrementAndGet = atomicInteger.incrementAndGet();
        if (incrementAndGet == 1 || incrementAndGet % 1000 == 0) {
            System.out.println("接受:" + msg.getSequenceId() + " 总数：" + incrementAndGet + ":" + System.currentTimeMillis());
        }

        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        MsgId msgId = new MsgId(channelSession.getSequenceNumber());
//        new Thread(() -> {
//            try {
//                Thread.sleep(15 * 60 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (incrementAndGet % 2 == 1) {
//                System.out.println("发送响应" + msgId);

                CmppSubmitResponseMessage response = new CmppSubmitResponseMessage(msg.getHeader());
                response.setMsgId(msgId);
                response.setResult(0);
                ctx.channel().writeAndFlush(response);
//            }

//        }).start();
//        MsgId msgId = response.getMsgId();
//
//
//        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
//        final EventExecutor executor = ctx.channel().pipeline().firstContext().executor();
//
//        executor.submit(() -> {
//            channelSession.sendMessage(response);
//        });
//

//        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
//        //模拟发送状态回执
//        CmppDeliverRequestMessage deliverRequestMessage = new CmppDeliverRequestMessage();
//
//        deliverRequestMessage.getHeader().setSequenceId(sequenceNumber.next());
//        deliverRequestMessage.setRegisteredDelivery((short) 1);
//        //状态信息
//        CmppReportRequestMessage report = new CmppReportRequestMessage();
//        report.setMsgId(msgId);
//        report.setStat("DELIVRD");
//        report.setDestterminalId(msg.getDestTerminalId()[0]);
//        report.setSubmitTime("2006221701");
//        report.setDoneTime("2006221701");
//        deliverRequestMessage.setReportRequestMessage(report);
////        executor.submit(() -> {
////            channelSession.sendMessage(deliverRequestMessage);
////        });
//        ctx.channel().writeAndFlush(deliverRequestMessage);

        //模拟发送上行短信
//        CmppDeliverRequestMessage mo = new CmppDeliverRequestMessage();
//        mo.getHeader().setSequenceId(sequenceNumber.next());
//        mo.setRegisteredDelivery((short) 0);
//        mo.setMsgContent("TEST");
//        ctx.channel().writeAndFlush(mo);
    }
}
