package com.drondea.sms.message.cmpp.codec;


import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppConnectResponseMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuyanning
 */
public class CmppConnectResponseMessageCodec implements ICodec {

    private final Logger logger = LoggerFactory.getLogger(CmppConnectResponseMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppConnectResponseMessage message = new CmppConnectResponseMessage((CmppHeader) header);
        long bodyLength = message.getHeader().getBodyLength();

        //不同版本的cmpp协议长度不同
        if (bodyLength == message.getBodyLength30()) {
            message.setStatus(bodyBuffer.readUnsignedInt());
            message.setAuthenticatorISMG(NettyByteBufUtil.toArray(bodyBuffer, 16));
            message.setVersion(bodyBuffer.readUnsignedByte());
        } else if (bodyLength == message.getBodyLength20()) {
            message.setStatus(bodyBuffer.readUnsignedByte());
            message.setAuthenticatorISMG(NettyByteBufUtil.toArray(bodyBuffer, 16));
            message.setVersion(bodyBuffer.readUnsignedByte());
        } else {
            logger.error("cmpp 登录响应包异常");
        }
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppConnectResponseMessage message = (CmppConnectResponseMessage) msg;
        long bodyLength = message.getHeader().getBodyLength();
        if (bodyLength == message.getBodyLength30()) {
            bodyBuffer.writeInt((int) message.getStatus());
        } else if (bodyLength == message.getBodyLength20()) {
            bodyBuffer.writeByte((int) message.getStatus());
        } else {
            logger.error("cmpp 登录包encode异常");
        }

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getAuthenticatorISMG(), 16));
        bodyBuffer.writeByte(message.getVersion());
        return bodyBuffer;
    }
}
