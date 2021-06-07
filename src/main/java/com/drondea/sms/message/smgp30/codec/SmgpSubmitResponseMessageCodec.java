package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.common.util.SmgpMsgIdUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitResponseMessage;
import io.netty.buffer.ByteBuf;

/**
 * @version V3.0
 * @description: 提交短信响应解码
 * @author: ywj
 * @date: 2020年06月08日17:01
 **/
public class SmgpSubmitResponseMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpSubmitResponseMessage message = new SmgpSubmitResponseMessage((SmgpHeader) header);
        message.setSmgpMsgId(SmgpMsgIdUtil.bytes2SmgpMsgId(NettyByteBufUtil.toArray(bodyBuffer, 10)));
        message.setStatus(bodyBuffer.readInt());

//        System.out.println( " 客户端收到提交响应  decode " + message.toString());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmgpSubmitResponseMessage message = (SmgpSubmitResponseMessage) msg;
        bodyBuffer.writeBytes(SmgpMsgIdUtil.msgId2Bytes(message.getSmgpMsgId()));
        bodyBuffer.writeInt((int) message.getStatus());

//        System.out.println( " 服务端发送提交响应  decode " + message.toString());
        return bodyBuffer;
    }
}
