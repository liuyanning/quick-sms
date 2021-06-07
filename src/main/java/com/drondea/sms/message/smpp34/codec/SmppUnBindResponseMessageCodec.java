package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppUnBindResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author: gengjinbiao
 **/
public class SmppUnBindResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmppUnBindResponseMessage message = new SmppUnBindResponseMessage((SmppHeader) header);
        return message;

    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
