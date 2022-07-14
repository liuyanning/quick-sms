package com.drondea.sms.message.smpp34.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppDeliverSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppReportRequestMessage;
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
public class SmppDeliverSmRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(SmppDeliverSmRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception {
        SmppDeliverSmRequestMessage message = new SmppDeliverSmRequestMessage((SmppHeader) header);
        try {
            message.setServiceType(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setSourceAddrTon(bodyBuffer.readByte());
            message.setSourceAddrNpi(bodyBuffer.readByte());
            message.setSourceAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setDestAddrTon(bodyBuffer.readByte());
            message.setDestAddrNpi(bodyBuffer.readByte());
            message.setDestinationAddr(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setEsmClass(bodyBuffer.readByte());
            message.setProtocolId(bodyBuffer.readByte());
            message.setPriorityFlag(bodyBuffer.readByte());
            message.setScheduleDeliveryTime(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setValidityPeriod(SmppUtil.readNullTerminatedString(bodyBuffer));
            message.setRegisteredDelivery(bodyBuffer.readByte());
            message.setReplaceIfPresentFlag(bodyBuffer.readByte());
            message.setDataCoding(bodyBuffer.readByte());
            message.setSmDefaultMsgIid(bodyBuffer.readByte());
            message.setSmLength(CommonUtil.byteToShort(bodyBuffer.readByte()));
            byte[] contentBytes = new byte[message.getSmLength()];
            bodyBuffer.readBytes(contentBytes);
            message.setShortMessage(contentBytes);
            while (bodyBuffer.readableBytes() > 0) {
                Tlv tlv = readTlv(bodyBuffer);
                message.addOptionalParameter(tlv);
            }

            //状态报告处理
            if (message.isReport()) {
                SmppReportRequestMessage reportRequestMessage = new SmppReportRequestMessage();
                reportRequestMessage.createReportMessage(message.getShortMessage());
                message.setReportRequest(reportRequestMessage);
            }
            return message;
        } catch (Exception e) {
            logger.error("smpp decode codec:", e);
            throw new InvalidMessageException("smpp decode codec Exception", message);
        }
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        SmppDeliverSmRequestMessage message = (SmppDeliverSmRequestMessage) msg;

        try {
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getServiceType());
            bodyBuffer.writeByte(message.getSourceAddrTon());
            bodyBuffer.writeByte(message.getSourceAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getSourceAddr());

            bodyBuffer.writeByte(message.getDestAddrTon());
            bodyBuffer.writeByte(message.getDestAddrNpi());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getDestinationAddr());

            bodyBuffer.writeByte(message.getEsmClass());
            bodyBuffer.writeByte(message.getProtocolId());
            bodyBuffer.writeByte(message.getPriorityFlag());

            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getScheduleDeliveryTime());
            SmppUtil.writeNullTerminatedString(bodyBuffer, message.getValidityPeriod());
            bodyBuffer.writeByte(message.getRegisteredDelivery());
            bodyBuffer.writeByte(message.getReplaceIfPresentFlag());
            bodyBuffer.writeByte(message.getDataCoding());
            bodyBuffer.writeByte(message.getSmDefaultMsgIid());
            bodyBuffer.writeByte(message.getSmLength());
            if (message.getShortMessage() != null) {
                bodyBuffer.writeBytes(message.getShortMessage());
            }
            if (message.getOptionalParameters() != null) {
                for (Tlv tlv : message.getOptionalParameters()) {
                    writeTlv(bodyBuffer, tlv);
                }
            }
            return bodyBuffer;
        } catch (Exception e) {
            logger.error("smpp codec encode:", e);
            return null;
        }
    }

}
