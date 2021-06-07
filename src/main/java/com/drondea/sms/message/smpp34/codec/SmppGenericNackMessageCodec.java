package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppGenericNackMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import io.netty.buffer.ByteBuf;

/**
 * @author: gengjinbiao
 **/
public class SmppGenericNackMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmppGenericNackMessage message = new SmppGenericNackMessage((SmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
