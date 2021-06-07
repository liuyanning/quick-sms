//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppBindTransceiverRequestMessage;
//import com.drondea.sms.message.smpp.SmppBindTransceiverResponseMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.SmppOutBindMessage;
//import com.drondea.sms.message.smpp.SmppUnBindRequestMessage;
//import com.drondea.sms.message.smpp.SmppUnBindResponseMessage;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppOutBindMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindResponseMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class bind {
//    public static void main(String[] args) {
//        SmppUnBindResponseMessage message = new SmppUnBindResponseMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_UNBIND_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppUnBindResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppUnBindResponseMessage deMessage = null;
//            deMessage = (SmppUnBindResponseMessage) SmppUnBindResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void unBindTest(){
//        SmppUnBindRequestMessage message = new SmppUnBindRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_UNBIND);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppUnBindRequestMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppUnBindRequestMessage deMessage = null;
//            deMessage = (SmppUnBindRequestMessage) SmppUnBindRequestMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void outBindTest(){
//        SmppOutBindMessage message = new SmppOutBindMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_OUTBIND);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setSystemId("SMPP3TEST");
//        message.setPassword("password1234");
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppOutBindMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppOutBindMessage deMessage = null;
//            deMessage = (SmppOutBindMessage) SmppOutBindMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
