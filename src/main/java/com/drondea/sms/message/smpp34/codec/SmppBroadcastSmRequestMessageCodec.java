package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppBroadcastSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
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
public class SmppBroadcastSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppBroadcastSmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppBroadcastSmRequestMessage message = new SmppBroadcastSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setPriorityFlag(bodyBuffer.readByte());
            message.setScheduleDeliveryTime(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setValidityPeriod(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setReplaceIfPresentFlag(bodyBuffer.readByte());
            message.setDataCoding(bodyBuffer.readByte());
            message.setSmDefaultMsgIid(bodyBuffer.readByte());
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
        SmppBroadcastSmRequestMessage message = (SmppBroadcastSmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getServiceType());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            bodyBuffer.writeByte(message.getPriorityFlag());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getScheduleDeliveryTime());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getValidityPeriod());
            bodyBuffer.writeByte(message.getReplaceIfPresentFlag());
            bodyBuffer.writeByte(message.getDataCoding());
            bodyBuffer.writeByte(message.getSmDefaultMsgIid());
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
