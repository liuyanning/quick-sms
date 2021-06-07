package com.drondea.sms.message.cmpp30.codec;

import com.drondea.sms.common.util.DefaultMsgIdUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0.0
 * @description: 心跳响应包编解码
 * @author: 刘彦宁
 * @date: 2020年06月08日17:01
 **/
public class CmppSubmitResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppSubmitResponseMessage message = new CmppSubmitResponseMessage((CmppHeader) header);
        message.setMsgId(DefaultMsgIdUtil.bytes2MsgId(NettyByteBufUtil.toArray(bodyBuffer, 8)));
        message.setResult(bodyBuffer.readUnsignedInt());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppSubmitResponseMessage message = (CmppSubmitResponseMessage) msg;
        bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(message.getMsgId()));
        bodyBuffer.writeInt((int) message.getResult());
        return bodyBuffer;
    }
}
