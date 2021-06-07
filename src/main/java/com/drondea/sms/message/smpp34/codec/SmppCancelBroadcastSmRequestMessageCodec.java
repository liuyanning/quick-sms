package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppCancelBroadcastSmRequestMessage;
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
public class SmppCancelBroadcastSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppCancelBroadcastSmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppCancelBroadcastSmRequestMessage message = new SmppCancelBroadcastSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
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
        SmppCancelBroadcastSmRequestMessage message = (SmppCancelBroadcastSmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getServiceType());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());

            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
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
