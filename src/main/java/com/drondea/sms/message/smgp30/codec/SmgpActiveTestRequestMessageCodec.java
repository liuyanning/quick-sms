package com.drondea.sms.message.smgp30.codec;


import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpActiveTestRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;

import io.netty.buffer.ByteBuf;

/**
 * @author ywj
 */
public class SmgpActiveTestRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpActiveTestRequestMessage message = new SmgpActiveTestRequestMessage((SmgpHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
