//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppDeliverSmRequestMessage;
//import com.drondea.sms.message.smpp.SmppDeliverSmResponseMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.SmppOutBindMessage;
//import com.drondea.sms.message.smpp.SmppUnBindRequestMessage;
//import com.drondea.sms.message.smpp.SmppUnBindResponseMessage;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppDeliverSmRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppDeliverSmResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppOutBindMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindResponseMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class deliver {
//
//    public static void main(String[] args) {
//
//        SmppDeliverSmResponseMessage message = new SmppDeliverSmResponseMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_DELIVER_SM_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setMessageId("aa");
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x01 },"a"));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppDeliverSmResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppDeliverSmResponseMessage deMessage = null;
//            deMessage = (SmppDeliverSmResponseMessage) SmppDeliverSmResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void DeliverSmRequestTest(){
//        SmppDeliverSmRequestMessage message = new SmppDeliverSmRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_DATA_SM);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setSmLength((byte)1);
//        message.setShortMessage("a".getBytes());
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x02 }));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppDeliverSmRequestMessageCodec.encode(message, compBuf);
//
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppDeliverSmRequestMessage deMessage = null;
//            deMessage = (SmppDeliverSmRequestMessage) SmppDeliverSmRequestMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//}
