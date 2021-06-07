package com.drondea.sms.message.sgip12.codec;


import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipBindResponseMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liyuehai
 */
public class SgipBindResponseMessageCodec implements ICodec {

    private final Logger logger = LoggerFactory.getLogger(SgipBindResponseMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipBindResponseMessage message = new SgipBindResponseMessage((SgipHeader) header);
        message.setResult(bodyBuffer.readUnsignedByte());
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipBindResponseMessage message = (SgipBindResponseMessage) msg;
        bodyBuffer.writeByte(message.getResult());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                8));
        return bodyBuffer;
    }
}
