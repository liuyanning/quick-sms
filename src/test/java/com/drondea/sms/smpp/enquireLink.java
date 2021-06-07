//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppEnquireLinkRequestMessage;
//import com.drondea.sms.message.smpp.SmppEnquireLinkResponseMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.SmppOutBindMessage;
//import com.drondea.sms.message.smpp.SmppUnBindRequestMessage;
//import com.drondea.sms.message.smpp.SmppUnBindResponseMessage;
//import com.drondea.sms.message.smpp.codec.SmppEnquireLinkRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppEnquireLinkResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppOutBindMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindResponseMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class enquireLink {
//    public static void main(String[] args) {
//        SmppEnquireLinkResponseMessage message = new SmppEnquireLinkResponseMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_ENQUIRE_LINK_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppEnquireLinkResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppEnquireLinkResponseMessage deMessage = null;
//            deMessage = (SmppEnquireLinkResponseMessage) SmppEnquireLinkResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void enquireLinkTest(){
//        SmppEnquireLinkRequestMessage message = new SmppEnquireLinkRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_ENQUIRE_LINK);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppEnquireLinkRequestMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppEnquireLinkRequestMessage deMessage = null;
//            deMessage = (SmppEnquireLinkRequestMessage) SmppEnquireLinkRequestMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//}
