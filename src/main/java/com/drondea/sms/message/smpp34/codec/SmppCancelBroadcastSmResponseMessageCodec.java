package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppCancelBroadcastSmResponseMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import io.netty.buffer.ByteBuf;

/**
 * @author: gengjinbiao
 **/
public class SmppCancelBroadcastSmResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmppCancelBroadcastSmResponseMessage message = new SmppCancelBroadcastSmResponseMessage((SmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
