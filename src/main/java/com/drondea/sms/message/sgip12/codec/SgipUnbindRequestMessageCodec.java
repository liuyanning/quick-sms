package com.drondea.sms.message.sgip12.codec;


import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipUnbindRequestMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author liyuehai
 */
public class SgipUnbindRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipUnbindRequestMessage message = new SgipUnbindRequestMessage((SgipHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
