//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppAlertNotificationMessage;
//import com.drondea.sms.message.smpp.SmppEnquireLinkRequestMessage;
//import com.drondea.sms.message.smpp.SmppEnquireLinkResponseMessage;
//import com.drondea.sms.message.smpp.SmppGenericNackMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppAlertNotificationMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppEnquireLinkRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppEnquireLinkResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppGenericNackMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class alert_notification_generic_nack {
//    public static void main(String[] args) {
//
//        SmppGenericNackMessage message = new SmppGenericNackMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_GENERIC_NACK);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppGenericNackMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppGenericNackMessage deMessage = null;
//            deMessage = (SmppGenericNackMessage) SmppGenericNackMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void AlertNotificationTest(){
//        SmppAlertNotificationMessage message = new SmppAlertNotificationMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_ALERT_NOTIFICATION);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_MS_AVAIL_STATUS, new byte[] { (byte)0x01 }));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppAlertNotificationMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppAlertNotificationMessage deMessage = null;
//            deMessage = (SmppAlertNotificationMessage) SmppAlertNotificationMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
