package com.drondea.sms.message.cmpp.codec;


import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.CmppTerminateRequestMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author liuyanning
 */
public class CmppTerminateRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppTerminateRequestMessage message = new CmppTerminateRequestMessage((CmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
