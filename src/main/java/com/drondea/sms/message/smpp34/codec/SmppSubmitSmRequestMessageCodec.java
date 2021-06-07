package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppSubmitSmRequestMessage;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gengjinbiao
 **/
public class SmppSubmitSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppSubmitSmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppSubmitSmRequestMessage message = new SmppSubmitSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));

            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));

            message.setEsmClass(bodyBuffer.readByte());
            message.setProtocolId(bodyBuffer.readByte());
            message.setPriorityFlag(bodyBuffer.readByte());

            message.setScheduleDeliveryTime(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setValidityPeriod(SmppUtil.readNullTerminatedString(bodyBuffer));

            message.setRegisteredDelivery(bodyBuffer.readByte());
            message.setReplaceIfPresentFlag(bodyBuffer.readByte());
            message.setDataCoding(bodyBuffer.readByte());
            message.setSmDefaultMsgIid(bodyBuffer.readByte());

            short msgLength = bodyBuffer.readUnsignedByte();
            message.setSmLength(msgLength);
            byte[] contentBytes = new byte[message.getSmLength()];
            bodyBuffer.readBytes(contentBytes);
            message.setShortMessage(contentBytes);
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppSubmitSmRequestMessage message = (SmppSubmitSmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getServiceType());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());

            bodyBuffer.writeByte(message.getDestAddrTon());
            bodyBuffer.writeByte(message.getDestAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getDestinationAddr());

            bodyBuffer.writeByte(message.getEsmClass());
            bodyBuffer.writeByte(message.getProtocolId());
            bodyBuffer.writeByte(message.getPriorityFlag());

            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getScheduleDeliveryTime());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getValidityPeriod());

            bodyBuffer.writeByte(message.getRegisteredDelivery());
            bodyBuffer.writeByte(message.getReplaceIfPresentFlag());
            bodyBuffer.writeByte(message.getDataCoding());
            bodyBuffer.writeByte(message.getSmDefaultMsgIid());
            bodyBuffer.writeByte(message.getSmLength());
            if (message.getShortMessage() != null) {
                bodyBuffer.writeBytes(message.getShortMessage());
            }
            return bodyBuffer;
        } catch (Exception e) {
            logger.error("smpp codec encode:", e);
            return null;
        }
    }


}
