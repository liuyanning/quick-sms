package com.drondea.sms.message.sgip12.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipReportRequestMessage;
import com.drondea.sms.type.SgipConstants;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author liyuehai
 */
public class SgipReportRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SgipReportRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SgipReportRequestMessage message = new SgipReportRequestMessage((SgipHeader) header);
        long nodeId = bodyBuffer.readUnsignedInt();
        int timestamp = bodyBuffer.readInt();
        int sequenceId = bodyBuffer.readInt();
        SgipSequenceNumber submitSequenceNumber = SgipSequenceNumber.wrapSequenceNumber(nodeId, timestamp, sequenceId);

        message.setSubmitSequenceNumber(submitSequenceNumber);
        message.setReportType(bodyBuffer.readUnsignedByte());
        message.setUserNumber(bodyBuffer.readCharSequence(21, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        message.setState(bodyBuffer.readUnsignedByte());
        message.setErrorCode(bodyBuffer.readUnsignedByte());
        message.setReserve(bodyBuffer.readCharSequence(8, SgipConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return message;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SgipReportRequestMessage message = (SgipReportRequestMessage) msg;
        bodyBuffer.writeInt((int)message.getSubmitSequenceNumber().getNodeId());
        bodyBuffer.writeInt(message.getSubmitSequenceNumber().getTimestamp());
        bodyBuffer.writeInt(message.getSubmitSequenceNumber().getSequenceId());
        bodyBuffer.writeByte(message.getReportType());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getUserNumber().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeByte(message.getState());
        bodyBuffer.writeByte(message.getErrorCode());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserve().
                getBytes(SgipConstants.DEFAULT_TRANSPORT_CHARSET), 8));
        return bodyBuffer;
    }
}
