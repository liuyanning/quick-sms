package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.common.util.SmgpMsgIdUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpDeliverResponseMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0
 * @description: smgp回执响应包编解码
 * @author: ywj
 * @date: 2020年06月08日17:01
 **/
public class SmgpDeliverResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpDeliverResponseMessage message = new SmgpDeliverResponseMessage((SmgpHeader) header);
        message.setSmgpMsgId(SmgpMsgIdUtil.bytes2SmgpMsgId(NettyByteBufUtil.toArray(bodyBuffer, 10)));
        message.setStatus(bodyBuffer.readInt());

//        System.out.println(" 回执响应 解码 --=-- " + message.toString());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmgpDeliverResponseMessage message = (SmgpDeliverResponseMessage) msg;
        bodyBuffer.writeBytes(SmgpMsgIdUtil.msgId2Bytes(message.getSmgpMsgId()));
        bodyBuffer.writeInt((int) message.getStatus());

//        System.out.println(" 回执响应 编码 --=-- " + message.toString());
        return bodyBuffer;
    }
}
