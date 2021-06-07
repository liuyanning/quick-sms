package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppQuerySmResponseMessage;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gengjinbiao
 **/
public class SmppQuerySmResponseMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppQuerySmResponseMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppQuerySmResponseMessage message = new SmppQuerySmResponseMessage((SmppHeader) header);
        try {
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setFinalDate(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setMessageState(bodyBuffer.readByte());
            message.setErrorCode(bodyBuffer.readByte());
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppQuerySmResponseMessage message = (SmppQuerySmResponseMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getFinalDate());
            bodyBuffer.writeByte(message.getMessageState());
            bodyBuffer.writeByte(message.getErrorCode());
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }


}
