package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppAlertNotificationMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.Tlv;
import io.netty.buffer.ByteBuf;

import static com.drondea.sms.common.util.SmppUtil.readTlv;
import static com.drondea.sms.common.util.SmppUtil.writeTlv;

/**
 * @author: gengjinbiao
 **/
public class SmppAlertNotificationMessageCodec implements ICodec {

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        SmppAlertNotificationMessage message = new SmppAlertNotificationMessage((SmppHeader) header);
        try {
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setEsmeAddrTon(bodyBuffer.readByte());
            message.setEsmeAddrNpi(bodyBuffer.readByte());
            message.setEsmeAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            while (bodyBuffer.readableBytes() > 0) {
                Tlv tlv = readTlv(bodyBuffer);
                message.addOptionalParameter(tlv);
            }
            return message;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppAlertNotificationMessage message = (SmppAlertNotificationMessage) msg;
        try {
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());
            bodyBuffer.writeByte(message.getEsmeAddrTon());
            bodyBuffer.writeByte(message.getEsmeAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getEsmeAddr());
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
