package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.smpp34.SmppDeliverSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppSubmitSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppSubmitSmResponseMessage;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 测试服务器接收短信数据
 * @author: gengjinbiao
 * @date: 2020年07月15日17:26
 **/
public class ServerSmppSubmitRequestHandler extends SimpleChannelInboundHandler<SmppSubmitSmRequestMessage> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerSmppSubmitRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmppSubmitSmRequestMessage msg) throws Exception {

        //是否长短信组装完毕
        boolean msgComplete = msg.isMsgComplete();
        if (msgComplete) {
            logger.debug("组装完毕，总共 {} 条，第 {} 条，短信内容：{}", msg.getPkTotal(), msg.getPkNumber(), msg.getMsgContent());
        } else {
            logger.debug("组装未完成，总共 {} 条，第 {} 条，短信内容：{}", msg.getPkTotal(), msg.getPkNumber(), msg.getMsgContent());
        }
        int incrementAndGet = atomicInteger.incrementAndGet();
        if (incrementAndGet == 1 || incrementAndGet % 1000 == 0) {
            System.out.println("接受:" + msg.getSequenceId() + " 总数：" + incrementAndGet + ":" + System.currentTimeMillis());
        }

        SmppSubmitSmResponseMessage response = new SmppSubmitSmResponseMessage(msg.getHeader());
        response.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
        ctx.channel().writeAndFlush(response);

        //执行发送
//        Thread.sleep(5000);
        System.out.println("短信发往完成，开始发送回执");

        //回执

//        MsgId msgId = response.getMsgId();
//
        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
//        //模拟发送状态回执
//        SmppDeliverSmRequestMessage deliverRequestMessage = new SmppDeliverSmRequestMessage();
//        deliverRequestMessage.getHeader().setSequenceNumber(sequenceNumber.next());
//        deliverRequestMessage.setSourceAddr(msg.getSourceAddr());
//        deliverRequestMessage.setDestinationAddr(msg.getDestinationAddr());
//        deliverRequestMessage.setEsmClass((short)4);
//
//        deliverRequestMessage.setSmLength(msg.getSmLength());
//        deliverRequestMessage.setShortMessage(msg.getShortMessage());
//        deliverRequestMessage.setRegisteredDelivery((short)0);
//        //状态信息
//        CmppReportRequestMessage report = new CmppReportRequestMessage();
//        report.setMsgId(msgId);
//        report.setStat("DELIVERED");
//        report.setDestterminalId(msg.getDestTerminalId()[0]);
//        report.setSubmitTime("2006221701");
//        report.setDoneTime("2006221701");
//        deliverRequestMessage.setReportRequestMessage(report);
//        System.out.println("发送回执了");
//        ctx.channel().writeAndFlush(deliverRequestMessage);
//        Thread.sleep(5000);
        System.out.println("模拟发送上行短信");
        //模拟发送上行短信
        SmppDeliverSmRequestMessage request = new SmppDeliverSmRequestMessage();
        request.getHeader().setSequenceNumber(sequenceNumber.next());
        request.setMsgContent("测试123");
        request.setSourceAddrTon((short) 1);
        request.setSourceAddrNpi((short) 1);
        request.setSourceAddr("15032618281");

        request.setDestAddrTon((short) 1);
        request.setDestAddrNpi((short) 1);

        request.setDestinationAddr("10001");
        ctx.channel().writeAndFlush(request);
        System.out.println("模拟发送端口连接完成");

    }
}
