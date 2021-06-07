package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppReplaceSmRequestMessage;
import com.drondea.sms.message.smpp34.Tlv;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drondea.sms.common.util.SmppUtil.readTlv;
import static com.drondea.sms.common.util.SmppUtil.writeTlv;

/**
 * @author: gengjinbiao
 **/
public class SmppReplaceSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppReplaceSmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppReplaceSmRequestMessage message = new SmppReplaceSmRequestMessage((SmppHeader) header);
        try {
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setScheduleDeliveryTime(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setValidityPeriod(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setRegisteredDelivery(bodyBuffer.readByte());
            message.setSmDefaultMsgIid(bodyBuffer.readByte());
            message.setSmLength(bodyBuffer.readByte());
            byte[] contentBytes = new byte[message.getSmLength()];
            bodyBuffer.readBytes(contentBytes);
            message.setShortMessage(contentBytes);
            while (bodyBuffer.readableBytes() > 0) {
                Tlv tlv = readTlv(bodyBuffer);
                message.addOptionalParameter(tlv);
            }
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppReplaceSmRequestMessage message = (SmppReplaceSmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getScheduleDeliveryTime());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getValidityPeriod());
            bodyBuffer.writeByte(message.getRegisteredDelivery());
            bodyBuffer.writeByte(message.getSmDefaultMsgIid());
            bodyBuffer.writeByte(message.getSmLength());
            if (message.getShortMessage() != null) {
                bodyBuffer.writeBytes(message.getShortMessage());
            }
            if (message.getOptionalParameters() != null) {
                for (Tlv tlv : message.getOptionalParameters()) {
                    writeTlv(bodyBuffer, tlv);
                }
            }
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }


}
