package com.drondea.sms.handler.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.conf.sgip.SgipClientSocketConfig;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.message.cmpp.CmppReportRequestMessage;
import com.drondea.sms.message.sgip12.SgipReportRequestMessage;
import com.drondea.sms.message.sgip12.SgipReportResponseMessage;
import com.drondea.sms.message.sgip12.SgipSubmitRequestMessage;
import com.drondea.sms.message.sgip12.SgipSubmitResponseMessage;
import com.drondea.sms.session.AbstractClientSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 测试服务器接收短信数据
 * @author: 刘彦宁
 * @date: 2020年06月18日17:26
 **/
public class ServerSgipSubmitRequestHandler extends SimpleChannelInboundHandler<SgipSubmitRequestMessage> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(ServerSgipSubmitRequestHandler.class);

    /**
     * 收到客户端提交的短信内容
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SgipSubmitRequestMessage msg) throws Exception {

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

        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        SgipSubmitResponseMessage response = new SgipSubmitResponseMessage(msg.getHeader());
        System.out.println("sequenceNumber:" + response.getHeader().getSequenceNumber());
        response.setResult((short) 0);
        channelSession.sendMessage(response);
    }
}
