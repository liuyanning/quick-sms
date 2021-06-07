package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.common.util.SmgpMsgIdUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpDeliverRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpReportMessage;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.SmgpConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * @author ywj
 */
public class SmgpDeliverRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmgpDeliverRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmgpDeliverRequestMessage requestMessage = new SmgpDeliverRequestMessage((SmgpHeader) header);
        requestMessage.setSmgpMsgId(SmgpMsgIdUtil.bytes2SmgpMsgId(NettyByteBufUtil.toArray(bodyBuffer, 10)));
        boolean isReport = bodyBuffer.readByte() == (byte) 1;
        requestMessage.setMsgFmt(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));
        requestMessage.setRecvTime(bodyBuffer.readCharSequence(14, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        requestMessage.setSrcTermId(bodyBuffer.readCharSequence(21, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        requestMessage.setDestTermId(bodyBuffer.readCharSequence(21, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        short msgLength = (short) (bodyBuffer.readUnsignedByte() & 0xffff);
        requestMessage.setMsgLength(msgLength);
        if (isReport && msgLength > 0) {//是状态报告
            SmgpReportMessage report = new SmgpReportMessage();

            String temp = bodyBuffer.readCharSequence("id:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setSmgpMsgId(SmgpMsgIdUtil.bytes2SmgpMsgId(NettyByteBufUtil.toArray(bodyBuffer, 10)));

            temp = bodyBuffer.readCharSequence(" sub:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setSub(bodyBuffer.readCharSequence(3, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" dlvrd:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setDlvrd(bodyBuffer.readCharSequence(3, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" submit date:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setSubTime(bodyBuffer.readCharSequence(10, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" done date:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setDoneTime(bodyBuffer.readCharSequence(10, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" stat:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setStat(bodyBuffer.readCharSequence(7, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" err:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setErr(bodyBuffer.readCharSequence(3, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            temp = bodyBuffer.readCharSequence(" text:".length(), SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString();
            report.setTxt(bodyBuffer.readCharSequence(20, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

            requestMessage.setReport(report);
        } else {//mo
            byte[] contentBytes = new byte[msgLength];
            bodyBuffer.readBytes(contentBytes);
            requestMessage.setBMsgContent(contentBytes);
        }
        requestMessage.setReserve(bodyBuffer.readCharSequence(8, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        //TLV 部分
        requestMessage.decodeTLV(bodyBuffer);

//        System.out.println(" 回执请求 解码 " + requestMessage.toString());
        return requestMessage;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) throws Exception {
        SmgpDeliverRequestMessage message = (SmgpDeliverRequestMessage) msg;
        bodyBuffer.writeBytes(SmgpMsgIdUtil.msgId2Bytes(message.getSmgpMsgId()));//10
        bodyBuffer.writeByte(message.isReport() ? (byte) 1 : (byte) 0);//1
        bodyBuffer.writeByte(message.getMsgFmt().getValue());//1
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getRecvTime().
                getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 14));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSrcTermId().
                getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getDestTermId().
                getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 21));

        if (message.isReport()) {//是状态报告
            SmgpReportMessage report = message.getReport();
            bodyBuffer.writeByte((short) SmgpReportMessage.LENGTH);//短消息长度
            bodyBuffer.writeBytes("id:".getBytes());
            bodyBuffer.writeBytes(SmgpMsgIdUtil.msgId2Bytes(report.getSmgpMsgId()));
            bodyBuffer.writeBytes(" sub:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getSub().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 3));
            bodyBuffer.writeBytes(" dlvrd:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getDlvrd().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 3));
            bodyBuffer.writeBytes(" submit date:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getSubTime().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 10));
            bodyBuffer.writeBytes(" done date:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getDoneTime().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 10));
            bodyBuffer.writeBytes(" stat:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getStat().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 7));
            bodyBuffer.writeBytes(" err:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getErr().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 3));
            bodyBuffer.writeBytes(" text:".getBytes());
            bodyBuffer.writeBytes(CommonUtil.ensureLength(report.getTxt().
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 20));
        } else {
            bodyBuffer.writeByte(message.getMsgLength());//短消息长度
            bodyBuffer.writeBytes(message.getBMsgContent());//短消息内容
        }

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().
                getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 8));

        //TLV 部分
        bodyBuffer = message.encodeTLV(bodyBuffer);

//        System.out.println(" 回执请求 编码 " + message.toString());
        return bodyBuffer;
    }
}
