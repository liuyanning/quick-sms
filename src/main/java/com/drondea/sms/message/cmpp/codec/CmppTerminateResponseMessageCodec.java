package com.drondea.sms.message.cmpp.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.CmppTerminateResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description: 断开连接响应包
 * @author: 刘彦宁
 * @date: 2020年06月17日17:16
 **/
public class CmppTerminateResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppTerminateResponseMessage message = new CmppTerminateResponseMessage((CmppHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
