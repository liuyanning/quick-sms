package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppReplaceSmResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author: gengjinbiao
 **/
public class SmppReplaceSmResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmppReplaceSmResponseMessage message = new SmppReplaceSmResponseMessage((SmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
