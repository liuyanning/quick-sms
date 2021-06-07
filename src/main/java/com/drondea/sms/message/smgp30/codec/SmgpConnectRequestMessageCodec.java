package com.drondea.sms.message.smgp30.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.type.SmgpConstants;

import io.netty.buffer.ByteBuf;

/**
 * smgp连接请求编解码器
 *
 * @author ywj
 */
public class SmgpConnectRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmgpConnectRequestMessage message = new SmgpConnectRequestMessage((SmgpHeader) header);
        //客户端id，ClientID，登录账号，即SP的企业代码，8个字节
        message.setClientId(bodyBuffer.readCharSequence(8, SmgpConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        //鉴权字节码,16字节
        message.setAuthenticatorClient(NettyByteBufUtil.toArray(bodyBuffer, 16));
        //登录模式，1
        message.setLoginMode(bodyBuffer.readByte());
        //时间戳，4
        message.setTimestamp(bodyBuffer.readUnsignedInt());
        //版本号，1
        message.setClientVersion(bodyBuffer.readUnsignedByte());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmgpConnectRequestMessage message = (SmgpConnectRequestMessage) msg;
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getClientId().getBytes(SmgpConstants.DEFAULT_TRANSPORT_CHARSET),
                8));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getAuthenticatorClient(), 16));
        bodyBuffer.writeByte(message.getLoginMode());
        bodyBuffer.writeInt((int) message.getTimestamp());
        bodyBuffer.writeByte(message.getClientVersion());

//        System.out.println(" smgp 登录 编码 ：" + message.toString());
        return bodyBuffer;
    }

}
