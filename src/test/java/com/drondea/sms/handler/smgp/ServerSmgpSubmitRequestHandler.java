package com.drondea.sms.handler.smgp;


import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SmgpMsgId;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpDeliverRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpReportMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0
 * @description: 测试服务器接收短信数据
 * @author: ywj
 * @date: 2020年06月18日17:26
 **/
public class ServerSmgpSubmitRequestHandler extends SimpleChannelInboundHandler<SmgpSubmitRequestMessage> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerSmgpSubmitRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmgpSubmitRequestMessage msg) throws Exception {
//        System.out.println("ServerSmgpSubmitRequestHandler channelRead0 接收submit 返回response");
        //是否长短信组装完毕
        boolean msgComplete = msg.isMsgComplete();
        if (msgComplete) {
            logger.debug("组装完毕，总共 {} 条，第 {} 条，短信内容：{}", msg.getPkTotal(), msg.getPkNumber(), msg.getMsgContent());
        } else {
            logger.debug("组装未完成，总共 {} 条，第 {} 条，短信内容：{}", msg.getPkTotal(), msg.getPkNumber(), msg.getMsgContent());
        }
        int incrementAndGet = atomicInteger.incrementAndGet();
//        if (incrementAndGet == 1 || incrementAndGet % 1000 == 0) {
        System.out.println("接受:" + msg.getSequenceId() + " 总数：" + incrementAndGet + ":" + System.currentTimeMillis());
//        }
        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        SmgpSubmitResponseMessage response = new SmgpSubmitResponseMessage(msg.getHeader());
        response.setSmgpMsgId(new SmgpMsgId(channelSession.getSequenceNumber()));
        response.setStatus(0);

        final EventExecutor executor = ctx.channel().pipeline().firstContext().executor();

        channelSession.sendMessage(response);


        SmgpMsgId msgId = response.getSmgpMsgId();

        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();

        //模拟发送状态回执
        SmgpDeliverRequestMessage deliverRequestMessage = new SmgpDeliverRequestMessage();

        deliverRequestMessage.getHeader().setSequenceId(sequenceNumber.next());
        deliverRequestMessage.setIsReport(true);
        deliverRequestMessage.setDestTermId(msg.getDestTermIdArray()[0]);//接收短信的号码
        //状态信息
        SmgpReportMessage report = new SmgpReportMessage();
        report.setSmgpMsgId(msgId);
        report.setStat("DELIVRD");//7个字节
        report.setSubTime("2006221701");
        report.setDoneTime("2006221701");
        deliverRequestMessage.setReport(report);//这里setReport，则isReport为true
        deliverRequestMessage.setReserve("Reserve");
//        deliverRequestMessage.setLinkId("LinkId");
//        deliverRequestMessage.setTpPid((byte)8);
//        deliverRequestMessage.setTpUdhi((byte)6);

        channelSession.sendMessage(deliverRequestMessage);

//        模拟发送上行短信
        SmgpDeliverRequestMessage mo = new SmgpDeliverRequestMessage();

        mo.getHeader().setSequenceId(sequenceNumber.next());
        mo.setIsReport(true);
        mo.getHeader().setSequenceId(sequenceNumber.next());
        mo.setIsReport(false);
        mo.setMsgContent("mo msg content test");
        //切分长短信
        List<IMessage> longMsgSlices = CommonUtil.getLongMsgSlices(mo, channelSession.getConfiguration(), sequenceNumber);
        longMsgSlices.forEach(deliverMsg -> {
            channelSession.sendMessage(deliverMsg);
        });
    }
}
