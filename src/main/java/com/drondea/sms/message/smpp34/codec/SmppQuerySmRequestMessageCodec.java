package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppQuerySmRequestMessage;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gengjinbiao
 **/
public class SmppQuerySmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppQuerySmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppQuerySmRequestMessage message = new SmppQuerySmRequestMessage((SmppHeader) header);
        try {
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppQuerySmRequestMessage message = (SmppQuerySmRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }


}
