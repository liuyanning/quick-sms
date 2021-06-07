package com.drondea.sms.handler.sgip;

import com.drondea.sms.message.sgip12.SgipDeliverRequestMessage;
import com.drondea.sms.message.sgip12.SgipDeliverResponseMessage;
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
public class ServerSgipDeliverRequestHandler extends SimpleChannelInboundHandler<SgipDeliverRequestMessage> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerSgipDeliverRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SgipDeliverRequestMessage msg) throws Exception {

        //是否长短信组装完毕
        boolean msgComplete = msg.isMsgComplete();
        if (msgComplete) {
            logger.debug("组装完毕，短信内容：{}", msg.getMsgContent());
        } else {
            logger.debug("组装未完成，短信内容：{}", msg.getMsgContent());
        }
        int incrementAndGet = atomicInteger.incrementAndGet();
        if (incrementAndGet == 1 || incrementAndGet % 1000 == 0) {
            System.out.println("接受:" + msg.getSequenceId() + " 总数：" + incrementAndGet + ":" + System.currentTimeMillis());
        }

        SgipDeliverResponseMessage response = new SgipDeliverResponseMessage(msg.getHeader());
        response.setResult((short) 0);
        ctx.channel().writeAndFlush(response);
//        MsgId msgId = response.getMsgId();


//        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
//        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
//        //模拟发送状态回执
//        CmppDeliverRequestMessage deliverRequestMessage = new CmppDeliverRequestMessage();
//
//        deliverRequestMessage.getHeader().setSequenceId(sequenceNumber.next());
//        deliverRequestMessage.setRegisteredDelivery((short) 1);
//        //状态信息
//        CmppReportRequestMessage report = new CmppReportRequestMessage();
//        report.setMsgId(msgId);
//        report.setStat("DELIVERED");
//        report.setDestterminalId(msg.getDestTerminalId()[0]);
//        report.setSubmitTime("2006221701");
//        report.setDoneTime("2006221701");
//        deliverRequestMessage.setReportRequestMessage(report);
//        ctx.channel().writeAndFlush(deliverRequestMessage);

        //模拟发送上行短信
//        CmppDeliverRequestMessage mo = new CmppDeliverRequestMessage();
//        mo.getHeader().setSequenceId(sequenceNumber.next());
//        mo.setRegisteredDelivery((short) 0);
//        mo.setMsgContent("TEST");
//        ctx.channel().writeAndFlush(mo);
    }
}
