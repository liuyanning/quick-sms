package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppCancelSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gengjinbiao
 **/
public class SmppCancelSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppCancelSmRequestMessageCodec.class);


    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppCancelSmRequestMessage message = new SmppCancelSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppCancelSmRequestMessage message = (SmppCancelSmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getServiceType());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
            bodyBuffer.writeByte(message.getDestAddrTon());
            bodyBuffer.writeByte(message.getDestAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getDestinationAddr());
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }


}
