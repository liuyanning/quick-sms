package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipBindResponseMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipReportResponseMessage;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description
 * @author: liyuehai
 * @date: 2020年06月08日17:01
 **/
public class SgipReportResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipReportResponseMessage message = new SgipReportResponseMessage((SgipHeader) header);
        message.setResult(bodyBuffer.readUnsignedByte());
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipReportResponseMessage message = (SgipReportResponseMessage) msg;
        bodyBuffer.writeByte(message.getResult());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET),
                8));
        return bodyBuffer;
    }
}
