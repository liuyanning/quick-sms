package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipUnbindResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description: 断开连接响应包
 * @author: liyuehai
 * @date: 2020年06月17日17:16
 **/
public class SgipUnbindResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipUnbindResponseMessage message = new SgipUnbindResponseMessage((SgipHeader) header);
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        return bodyBuffer;
    }
}
