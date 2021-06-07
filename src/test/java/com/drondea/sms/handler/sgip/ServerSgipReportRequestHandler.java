package com.drondea.sms.handler.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.sgip12.SgipReportRequestMessage;
import com.drondea.sms.message.sgip12.SgipReportResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 测试服务器接收短信数据
 * @author: liyuehai
 * @date: 2020年06月18日17:26
 **/
public class ServerSgipReportRequestHandler extends SimpleChannelInboundHandler<SgipReportRequestMessage> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerSgipReportRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SgipReportRequestMessage msg) throws Exception {

        //是否长短信组装完毕
        logger.debug("msgId: {}, status: {}, errCode: {}", msg.getSubmitSequenceNumber(), msg.getState(), msg.getErrorCode());
        int incrementAndGet = atomicInteger.incrementAndGet();
        if (incrementAndGet == 1 || incrementAndGet % 1000 == 0) {
            System.out.println("接受:" + msg.getSequenceId() + " 总数：" + incrementAndGet + ":" + System.currentTimeMillis());
        }

        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        SgipReportResponseMessage response = new SgipReportResponseMessage(msg.getHeader());
        response.setResult((short) 0);
//        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
//        System.out.println(channelFuture);
        channelSession.sendMessage(response);
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
