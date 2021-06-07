package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppSubmitMultiResponseMessage;
import com.drondea.sms.message.smpp34.Tlv;
import com.drondea.sms.type.InvalidMessageException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drondea.sms.common.util.SmppUtil.readTlv;
import static com.drondea.sms.common.util.SmppUtil.writeTlv;

/**
 * @author: gengjinbiao
 **/
public class SmppSubmitMultiResponseMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppSubmitMultiResponseMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppSubmitMultiResponseMessage message = new SmppSubmitMultiResponseMessage((SmppHeader) header);
        try {
            message.setMessageId(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setNoUnsuccess(bodyBuffer.readByte());

            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setErrorStatusCode(bodyBuffer.readInt());

            while (bodyBuffer.readableBytes() > 0) {
                Tlv tlv = readTlv(bodyBuffer);
                message.addOptionalParameter(tlv);
            }
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }

    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppSubmitMultiResponseMessage message = (SmppSubmitMultiResponseMessage) msg;
        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getMessageId());
            bodyBuffer.writeByte(message.getNoUnsuccess());

            bodyBuffer.writeByte(message.getDestAddrTon());
            bodyBuffer.writeByte(message.getDestAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getDestinationAddr());
            bodyBuffer.writeInt(message.getErrorStatusCode());

            if (message.getOptionalParameters() != null) {
                for (Tlv tlv : message.getOptionalParameters()) {
                    writeTlv(bodyBuffer, tlv);
                }
            }
            return bodyBuffer;
        } catch (Exception e) {
            return null;
        }
    }


}
