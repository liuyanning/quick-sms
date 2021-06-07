package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipDeliverRequestMessage;
import com.drondea.sms.message.sgip12.SgipHeader;

import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;
import com.drondea.sms.thirdparty.SmsDcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author liyuehai
 */
public class SgipDeliverRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SgipDeliverRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipDeliverRequestMessage message = new SgipDeliverRequestMessage((SgipHeader) header);
        message.setUserNumber(bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setSpNumber(bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setTpPid(bodyBuffer.readUnsignedByte());
        message.setTpUdhi(bodyBuffer.readUnsignedByte());
        message.setMessageCoding(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));

        int msgLength = bodyBuffer.readInt();
        byte[] contentbytes = new byte[msgLength];
        bodyBuffer.readBytes(contentbytes);
        message.setMessageLength(msgLength);
        message.setMsgContentBytes(contentbytes);
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipDeliverRequestMessage message = (SgipDeliverRequestMessage) msg;
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getUserNumber().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSpNumber().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeByte(message.getTpPid());
        bodyBuffer.writeByte(message.getTpUdhi());
        bodyBuffer.writeByte(message.getMessageCoding().getValue());
        bodyBuffer.writeInt(message.getMessageLength());
        bodyBuffer.writeBytes(message.getMsgContentBytes());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 8));
        return bodyBuffer;
    }
}
