package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppDataSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.Tlv;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drondea.sms.common.util.SmppUtil.readTlv;

/**
 * @author: gengjinbiao
 **/
public class SmppDataSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppDataSmRequestMessageCodec.class);


    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppDataSmRequestMessage message = new SmppDataSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setEsmClass(bodyBuffer.readByte());
            message.setRegisteredDelivery(bodyBuffer.readByte());
            message.setDataCoding(bodyBuffer.readByte());
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
        SmppDataSmRequestMessage message = (SmppDataSmRequestMessage) msg;
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setEsmClass(bodyBuffer.readByte());
            message.setRegisteredDelivery(bodyBuffer.readByte());
            message.setDataCoding(bodyBuffer.readByte());
            while (bodyBuffer.readableBytes() > 0) {
                Tlv tlv = readTlv(bodyBuffer);
                message.addOptionalParameter(tlv);
            }
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }
}
