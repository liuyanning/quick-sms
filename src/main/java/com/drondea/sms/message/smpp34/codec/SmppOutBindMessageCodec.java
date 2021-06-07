package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppBindTransceiverRequestMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppOutBindMessage;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gengjinbiao
 **/
public class SmppOutBindMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppOutBindMessageCodec.class);


    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppOutBindMessage message = new SmppOutBindMessage((SmppHeader) header);
        try {
            message.setSystemId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setPassword(SmppUtil.readNullTerminatedString(bodyBuffer));
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppBindTransceiverRequestMessage message = (SmppBindTransceiverRequestMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSystemId());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getPassword());
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }
}
