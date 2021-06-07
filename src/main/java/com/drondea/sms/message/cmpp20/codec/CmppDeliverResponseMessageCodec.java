package com.drondea.sms.message.cmpp20.codec;

import com.drondea.sms.common.util.DefaultMsgIdUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppDeliverResponseMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import io.netty.buffer.ByteBuf;

import static com.drondea.sms.common.util.NettyByteBufUtil.toArray;

/**
 * @version V3.0.0
 * @description: 心跳响应包编解码
 * @author: 刘彦宁
 * @date: 2020年06月08日17:01
 **/
public class CmppDeliverResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppDeliverResponseMessage message = new CmppDeliverResponseMessage((CmppHeader) header);
        message.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer, 8)));
        message.setResult(bodyBuffer.readUnsignedByte());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppDeliverResponseMessage message = (CmppDeliverResponseMessage) msg;
        bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(message.getMsgId()));
        bodyBuffer.writeByte((int) message.getResult());
        return bodyBuffer;
    }
}
