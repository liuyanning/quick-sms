package com.drondea.sms.message.cmpp.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.NettyByteBufUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppConnectRequestMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.type.CmppConstants;
import io.netty.buffer.ByteBuf;

/**
 * @author liuyanning
 */
public class CmppConnectRequestMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppConnectRequestMessage message = new CmppConnectRequestMessage((CmppHeader) header);
        //源地址，此处为SP_Id，即SP的企业代码。
        message.setSourceAddr(bodyBuffer.readCharSequence(6, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        //鉴权字节码
        message.setAuthenticatorSource(NettyByteBufUtil.toArray(bodyBuffer, 16));
        //版本号
        message.setVersion(bodyBuffer.readUnsignedByte());
        //时间戳
        message.setTimestamp(bodyBuffer.readUnsignedInt());

        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppConnectRequestMessage message = (CmppConnectRequestMessage) msg;
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSourceAddr().getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET),
                6));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getAuthenticatorSource(), 16));
        bodyBuffer.writeByte(message.getVersion());
        bodyBuffer.writeInt((int) message.getTimestamp());
        return bodyBuffer;
    }
}
