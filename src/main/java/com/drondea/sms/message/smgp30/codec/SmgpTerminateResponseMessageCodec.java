package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpTerminateResponseMessage;

import io.netty.buffer.ByteBuf;

/**
 * @version V3.0
 * @description: smgp断开连接响应包
 * @author: ywj
 * @date: 2020年06月17日17:16
 **/
public class SmgpTerminateResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpTerminateResponseMessage message = new SmgpTerminateResponseMessage((SmgpHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
