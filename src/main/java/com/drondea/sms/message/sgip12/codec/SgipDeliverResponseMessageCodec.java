package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipDeliverResponseMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description: deliver的响应包编解码
 * @author: 刘彦宁
 * @date: 2020年06月08日17:01
 **/
public class SgipDeliverResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipDeliverResponseMessage message = new SgipDeliverResponseMessage((SgipHeader) header);
        message.setResult(bodyBuffer.readUnsignedByte());
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipDeliverResponseMessage message = (SgipDeliverResponseMessage) msg;
        bodyBuffer.writeByte(message.getResult());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                8));
        return bodyBuffer;
    }
}
