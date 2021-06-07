package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpActiveTestResponseMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;

import io.netty.buffer.ByteBuf;

/**
 * @version V3.0
 * @description: smgp心跳响应包编解码
 * @author: ywj
 * @date: 2020年06月08日17:01
 **/
public class SmgpActiveTestResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpActiveTestResponseMessage message = new SmgpActiveTestResponseMessage((SmgpHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
