package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipSubmitRequestMessage;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;
import com.drondea.sms.thirdparty.SmsDcs;

/**
 * @author liuyanning
 */
public class SgipSubmitRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {

        SgipSubmitRequestMessage message = new SgipSubmitRequestMessage((SgipHeader) header);
        message.setSpNumber(bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setChargeNumber(bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setUserCount(bodyBuffer.readUnsignedByte());
        int usercount = message.getUserCount();
        String[] nums = new String[usercount];
        for (int i = 0; i < usercount; i++) {
            nums[i] = bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET)
                    .toString().trim();
        }
        message.setUserNumber(nums);

        message.setCorpId(bodyBuffer.readCharSequence(5, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setServiceType(bodyBuffer.readCharSequence(10, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setFeeType(bodyBuffer.readUnsignedByte());
        message.setFeeValue(bodyBuffer.readCharSequence(6, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setGivenValue(bodyBuffer.readCharSequence(6, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setAgentFlag(bodyBuffer.readUnsignedByte());
        message.setMorelatetomtFlag(bodyBuffer.readUnsignedByte());
        message.setPriority(bodyBuffer.readUnsignedByte());
        message.setExpireTime(bodyBuffer.readCharSequence(16, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setScheduleTime(bodyBuffer.readCharSequence(16, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setReportFlag(bodyBuffer.readUnsignedByte());
        message.setTpPid(bodyBuffer.readUnsignedByte());
        message.setTpUdhi(bodyBuffer.readUnsignedByte());
        message.setMessageCoding(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));
        message.setMessageType(bodyBuffer.readUnsignedByte());

        int msgLength = bodyBuffer.readInt();
        byte[] contentbytes = new byte[msgLength];
        bodyBuffer.readBytes(contentbytes);
        message.setMessageLength(msgLength);
        message.setMsgContentBytes(contentbytes);
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipSubmitRequestMessage message = (SgipSubmitRequestMessage) msg;
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSpNumber().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getChargeNumber().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        int usercount = message.getUserCount();
        bodyBuffer.writeByte(usercount);
        for (int i = 0; i < usercount; i++) {
            String[] destTermId = message.getUserNumber();
            bodyBuffer.writeBytes(CommonUtil.ensureLength(destTermId[i].getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                    21));
        }

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getCorpId().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 5));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getServiceType().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 10));
        bodyBuffer.writeByte(message.getFeeType());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getFeeValue().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 6));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getGivenValue().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 6));
        bodyBuffer.writeByte(message.getAgentFlag());
        bodyBuffer.writeByte(message.getMorelatetomtFlag());
        bodyBuffer.writeByte(message.getPriority());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getExpireTime().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 16));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getScheduleTime().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 16));
        bodyBuffer.writeByte(message.getReportFlag());
        bodyBuffer.writeByte(message.getTpPid());
        bodyBuffer.writeByte(message.getTpUdhi());
        bodyBuffer.writeByte(message.getMessageCoding().getValue());
        bodyBuffer.writeByte(message.getMessageType());
        bodyBuffer.writeInt(message.getMessageLength());
        bodyBuffer.writeBytes(message.getMsgContentBytes());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().getBytes
                (SgipConstants.DEFAULT_TRANSPORT_CHARSET), 8));
        return bodyBuffer;
    }

}
