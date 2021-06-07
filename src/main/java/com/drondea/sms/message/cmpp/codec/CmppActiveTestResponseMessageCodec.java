package com.drondea.sms.message.cmpp.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppActiveTestResponseMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description: 心跳响应包编解码
 * @author: 刘彦宁
 * @date: 2020年06月08日17:01
 **/
public class CmppActiveTestResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppActiveTestResponseMessage message = new CmppActiveTestResponseMessage((CmppHeader) header);
        if (bodyBuffer.readableBytes() > 0) {
            message.setReserved(bodyBuffer.readByte());
        }
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppActiveTestResponseMessage message = (CmppActiveTestResponseMessage) msg;
        bodyBuffer.writeByte(message.getReserved());
        return bodyBuffer;
    }
}
