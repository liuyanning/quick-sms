package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipBindRequestMessage;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;

/**
 * @author liyuehai
 */
public class SgipBindRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipBindRequestMessage message = new SgipBindRequestMessage((SgipHeader) header);
        message.setLoginType(bodyBuffer.readUnsignedByte());
        message.setLoginName(bodyBuffer.readCharSequence(16, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setLoginPassowrd(bodyBuffer.readCharSequence(16, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipBindRequestMessage message = (SgipBindRequestMessage) msg;

        bodyBuffer.writeByte(message.getLoginType());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getLoginName().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                16));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getLoginPassowrd().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                16));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                8));
        return bodyBuffer;
    }
}
