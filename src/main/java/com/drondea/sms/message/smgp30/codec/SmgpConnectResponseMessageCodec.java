package com.drondea.sms.message.smgp30.codec;


import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectResponseMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * smgp连接响应编解码器
 *
 * @author ywj
 */
public class SmgpConnectResponseMessageCodec implements ICodec {

    private final Logger logger = LoggerFactory.getLogger(SmgpConnectResponseMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpConnectResponseMessage message = new SmgpConnectResponseMessage((SmgpHeader) header);
        message.setStatus(bodyBuffer.readUnsignedInt());
        message.setAuthenticatorServer(NettyByteBufUtil.toArray(bodyBuffer, 16));
        message.setServerVersion(bodyBuffer.readUnsignedByte());

//        System.out.println(" smgp 登录 解码 " + message.toString());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmgpConnectResponseMessage message = (SmgpConnectResponseMessage) msg;
        bodyBuffer.writeInt((int) message.getStatus());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getAuthenticatorServer(), 16));
        bodyBuffer.writeByte(message.getServerVersion());
        return bodyBuffer;
    }
}
