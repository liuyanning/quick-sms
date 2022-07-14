package com.drondea.sms.message.smgp30.codec;


import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpExitRequestMessage;

import io.netty.buffer.ByteBuf;

/**
 * @author ywj
 */
public class SmgpTerminateRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpExitRequestMessage message = new SmgpExitRequestMessage((SmgpHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
