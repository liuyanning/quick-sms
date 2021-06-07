package com.drondea.sms.message.cmpp30.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.DefaultMsgIdUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.type.CmppConstants;
import io.netty.buffer.ByteBuf;
import com.drondea.sms.thirdparty.SmsDcs;

/**
 * @author liuyanning
 */
public class CmppSubmitRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {

        CmppSubmitRequestMessage message = new CmppSubmitRequestMessage((CmppHeader) header);
        message.setMsgId(DefaultMsgIdUtil.bytes2MsgId(NettyByteBufUtil.toArray(bodyBuffer, 8)));

        message.setPkTotal(bodyBuffer.readUnsignedByte());
        message.setPkNumber(bodyBuffer.readUnsignedByte());

        message.setRegisteredDelivery(bodyBuffer.readUnsignedByte());
        message.setMsgLevel(bodyBuffer.readUnsignedByte());
        message.setServiceId(bodyBuffer.readCharSequence(10, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setFeeUserType(bodyBuffer.readUnsignedByte());

        message.setFeeTerminalId(bodyBuffer.readCharSequence(32, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setFeeTerminalType(bodyBuffer.readUnsignedByte());

        message.setTpPid(bodyBuffer.readUnsignedByte());
        message.setTpUdhi(bodyBuffer.readUnsignedByte());
        message.setMsgFmt(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));

        message.setMsgSrc(bodyBuffer.readCharSequence(6, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setFeeType(bodyBuffer.readCharSequence(2, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setFeeCode(bodyBuffer.readCharSequence(6, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setValIdTime(bodyBuffer.readCharSequence(17, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setAtTime(bodyBuffer.readCharSequence(17, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        message.setSrcId(bodyBuffer.readCharSequence(21, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        short destUsrTl = bodyBuffer.readUnsignedByte();
        message.setDestUsrTl(destUsrTl);
        String[] destTermId = new String[destUsrTl];
        for (int i = 0; i < destUsrTl; i++) {
            destTermId[i] = bodyBuffer.readCharSequence(32, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim();
        }
        message.setDestTerminalId(destTermId);

        message.setDestTerminalType(bodyBuffer.readUnsignedByte());

        short msgLength = (short) (bodyBuffer.readUnsignedByte() & 0xffff);
//        System.out.println("消息长度：" + msgLength);
        byte[] contentBytes = new byte[msgLength];
        bodyBuffer.readBytes(contentBytes);
        message.setMsgContentBytes(contentBytes);
        message.setMsgLength(msgLength);

        message.setLinkId(bodyBuffer.readCharSequence(20, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppSubmitRequestMessage message = (CmppSubmitRequestMessage) msg;
        bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(message.getMsgId()));

        bodyBuffer.writeByte(message.getPkTotal());
        bodyBuffer.writeByte(message.getPkNumber());
        bodyBuffer.writeByte(message.getRegisteredDelivery());
        bodyBuffer.writeByte(message.getMsgLevel());

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getServiceId().getBytes
                (CmppConstants.DEFAULT_TRANSPORT_CHARSET), 10));

        bodyBuffer.writeByte(message.getFeeUserType());

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getFeeTerminalId().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 32));
        bodyBuffer.writeByte(message.getFeeTerminalType());
        bodyBuffer.writeByte(message.getTpPid());
        bodyBuffer.writeByte(message.getTpUdhi());
        bodyBuffer.writeByte(message.getMsgFmt().getValue());

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getMsgSrc().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 6));

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getFeeType().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 2, 0));

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getFeeCode().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 6));

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getValIdTime().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 17));

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getAtTime().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 17));

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSrcId().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 21));

        bodyBuffer.writeByte(message.getDestUsrTl());
        for (int i = 0; i < message.getDestUsrTl(); i++) {
            String[] destTermId = message.getDestTerminalId();
            bodyBuffer.writeBytes(CommonUtil.ensureLength(destTermId[i].
                    getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 32));
        }
        bodyBuffer.writeByte(message.getDestTerminalType());

        bodyBuffer.writeByte(message.getMsgLength());

//		System.out.println(message.getMsgContentBytes().length);
        bodyBuffer.writeBytes(message.getMsgContentBytes());

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getLinkId().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 20));
        return bodyBuffer;
    }
}
