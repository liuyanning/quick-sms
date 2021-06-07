package com.drondea.sms.message.cmpp.codec;


import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppActiveTestRequestMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import io.netty.buffer.ByteBuf;

/**
 * @author liuyanning
 */
public class CmppActiveTestRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppActiveTestRequestMessage message = new CmppActiveTestRequestMessage((CmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
