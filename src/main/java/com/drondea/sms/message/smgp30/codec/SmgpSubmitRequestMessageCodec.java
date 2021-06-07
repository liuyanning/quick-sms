package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.type.SmgpConstants;

import com.drondea.sms.thirdparty.SmsDcs;

import io.netty.buffer.ByteBuf;

/**
 * @author ywj
 */
public class SmgpSubmitRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmgpSubmitRequestMessage message = new SmgpSubmitRequestMessage((SmgpHeader) header);
        message.setMsgType(bodyBuffer.readByte());
        message.setNeedReport(bodyBuffer.readByte() == (short) 1);//short
        message.setPriority(bodyBuffer.readByte());
        message.setServiceId(bodyBuffer.readCharSequence(10, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setFeeType(bodyBuffer.readCharSequence(2, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setFeeCode(bodyBuffer.readCharSequence(6, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        if (0x13 != SmgpConstants.DEFAULT_VERSION) {
            message.setFixedFee(bodyBuffer.readCharSequence(6, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        }
        message.setMsgFmt(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));
        message.setValidTime(bodyBuffer.readCharSequence(17, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setAtTime(bodyBuffer.readCharSequence(17, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setSrcTermId(bodyBuffer.readCharSequence(21, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setChargeTermId(bodyBuffer.readCharSequence(21, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        short destTermIdCount = bodyBuffer.readUnsignedByte();
        message.setDestTermIdCount(destTermIdCount);
        String[] destTermId = new String[destTermIdCount];
        for (int i = 0; i < destTermIdCount; i++) {
            destTermId[i] = bodyBuffer.readCharSequence(21, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim();
        }
        message.setDestTermIdArray(destTermId);

        short msgLength = (short) (bodyBuffer.readUnsignedByte() & 0xffff);
        byte[] contentBytes = new byte[msgLength];
        bodyBuffer.readBytes(contentBytes);
        message.setMsgContentBytes(contentBytes);
        message.setReserve(bodyBuffer.readCharSequence(8, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        //SmgpTLV 部分
        message.decodeTLV(bodyBuffer);

//        System.out.println( " 服务端接收短信 解码  decode - - - - " + message.toString());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage message, ByteBuf bodyBuffer) throws Exception {
        SmgpSubmitRequestMessage msg = (SmgpSubmitRequestMessage) message;
        bodyBuffer.writeByte(msg.getMsgType());
        bodyBuffer.writeByte(msg.getNeedReport() ? (short) 1 : (short) 0);//short
        bodyBuffer.writeByte(msg.getPriority());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getServiceId().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 10));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getFeeType().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 2));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getFeeCode().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 6));
        if (0x13 != SmgpConstants.DEFAULT_VERSION) {
            bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getFixedFee().getBytes
                    (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 6));
        }
        bodyBuffer.writeByte(msg.getMsgFmt().getValue());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getValidTime().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 17));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getAtTime().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 17));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getSrcTermId().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getChargeTermId().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeByte(msg.getDestTermIdCount());

        for (int i = 0; i < msg.getDestTermIdCount(); i++) {
            String[] destTermId = msg.getDestTermIdArray();
            bodyBuffer.writeBytes(CommonUtil.ensureLength(destTermId[i].
                    getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        }
        bodyBuffer.writeByte(msg.getMsgLength());
        bodyBuffer.writeBytes(msg.getMsgContentBytes());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(msg.getReserve().getBytes
                (SmgpConstants.DEFAULT_TRANSPORT_CHARSET), 8));

        //SmgpTLV 部分
        msg.encodeTLV(bodyBuffer);

//        System.out.println( " 客户端提交短信 编码 encode - - - - " + msg.toString());
        return bodyBuffer;
    }

}
