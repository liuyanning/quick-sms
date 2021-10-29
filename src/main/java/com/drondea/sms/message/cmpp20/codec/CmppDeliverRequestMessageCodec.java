package com.drondea.sms.message.cmpp20.codec;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.DefaultMsgIdUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.CmppReportRequestMessage;
import com.drondea.sms.type.CmppConstants;
import io.netty.buffer.ByteBuf;
import com.drondea.sms.thirdparty.SmsDcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drondea.sms.common.util.NettyByteBufUtil.toArray;

/**
 * @author liuyanning
 */
public class CmppDeliverRequestMessageCodec implements ICodec {

    private static final Logger logger = LoggerFactory.getLogger(CmppDeliverRequestMessageCodec.class);

    @Override
    public IMessage decode(IHeader header, ByteBuf bodyBuffer) {
        CmppDeliverRequestMessage requestMessage = new CmppDeliverRequestMessage((CmppHeader) header);
        requestMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer, 8)));
        requestMessage.setDestId(bodyBuffer.readCharSequence(21, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        requestMessage.setServiceid(bodyBuffer.readCharSequence(10, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());

        requestMessage.setTpPid(bodyBuffer.readUnsignedByte());
        requestMessage.setTpUdhi(bodyBuffer.readUnsignedByte());
        requestMessage.setMsgFmt(new SmsDcs((byte) bodyBuffer.readUnsignedByte()));

        requestMessage.setSrcTerminalId(bodyBuffer.readCharSequence(21, CmppConstants.DEFAULT_TRANSPORT_CHARSET)
                .toString().trim());
        short registeredDelivery = bodyBuffer.readUnsignedByte();
        requestMessage.setRegisteredDelivery(registeredDelivery);

        int frameLength = (short) (bodyBuffer.readUnsignedByte() & 0xffff);

        requestMessage.setMsgLength((short) frameLength);
        //registeredDelivery == 0为非状态报告,registeredDelivery == 1 为状态报告
        if (registeredDelivery == 0) {
            byte[] contentbytes = new byte[frameLength];
            bodyBuffer.readBytes(contentbytes);
            requestMessage.setMsgContentBytes(contentbytes);
        } else {
            int destTerminalIdLen = 60;
            if (frameLength != destTerminalIdLen) {
                logger.warn("CmppDeliverRequestMessage - MsgContent length is {}. should be {}.", frameLength, destTerminalIdLen);
            }
            CmppReportRequestMessage cmppReportRequestMessage = new CmppReportRequestMessage();
            cmppReportRequestMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer, 8)));
            cmppReportRequestMessage.setStat(
                    bodyBuffer.readCharSequence(7, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
            cmppReportRequestMessage.setSubmitTime(
                    bodyBuffer.readCharSequence(10, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
            cmppReportRequestMessage.setDoneTime(
                    bodyBuffer.readCharSequence(10, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
            cmppReportRequestMessage.setDestterminalId(
                    bodyBuffer.readCharSequence(21, CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
            cmppReportRequestMessage.setSmscSequence(bodyBuffer.readUnsignedInt());
            requestMessage.setReportRequestMessage(cmppReportRequestMessage);
        }
        //剩下的字节全部读取
        requestMessage.setReserved(bodyBuffer.readCharSequence(bodyBuffer.readableBytes(), CmppConstants.DEFAULT_TRANSPORT_CHARSET).toString().trim());
        return requestMessage;
    }

    @Override
    public ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) {
        CmppDeliverRequestMessage message = (CmppDeliverRequestMessage) msg;
        bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(message.getMsgId()));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getDestId().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 21));
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getServiceid().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 10));
        bodyBuffer.writeByte(message.getTpPid());
        bodyBuffer.writeByte(message.getTpUdhi());
        bodyBuffer.writeByte(message.getMsgFmt().getValue());
        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getSrcTerminalId().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 21));

        short registeredDelivery = message.getRegisteredDelivery();
        bodyBuffer.writeByte(registeredDelivery);

        //registeredDelivery == 0 非状态报告，否则是状态报告
        if (registeredDelivery == 0) {
            bodyBuffer.writeByte(message.getMsgLength());

            bodyBuffer.writeBytes(message.getMsgContentBytes());

        } else {
            bodyBuffer.writeByte(60);
            bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(message.getReportRequestMessage().getMsgId()));
            bodyBuffer.writeBytes(CommonUtil.ensureLength(
                    message.getReportRequestMessage().getStat().
                            getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 7));
            bodyBuffer.writeBytes(CommonUtil.ensureLength(
                    message.getReportRequestMessage().getSubmitTime().
                            getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 10));
            bodyBuffer.writeBytes(CommonUtil.ensureLength(
                    message.getReportRequestMessage().getDoneTime().
                            getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 10));
            bodyBuffer.writeBytes(CommonUtil.ensureLength(
                    message.getReportRequestMessage().getDestterminalId().
                            getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 21));

            bodyBuffer.writeInt((int) message.getReportRequestMessage().getSmscSequence());
        }

        bodyBuffer.writeBytes(CommonUtil.ensureLength(message.getReserved().
                getBytes(CmppConstants.DEFAULT_TRANSPORT_CHARSET), 8));
        return bodyBuffer;
    }
}
