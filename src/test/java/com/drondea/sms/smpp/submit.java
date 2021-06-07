//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.SmppOutBindMessage;
//import com.drondea.sms.message.smpp.SmppSubmitMultiRequestMessage;
//import com.drondea.sms.message.smpp.SmppSubmitMultiResponseMessage;
//import com.drondea.sms.message.smpp.SmppSubmitSmRequestMessage;
//import com.drondea.sms.message.smpp.SmppSubmitSmResponseMessage;
//import com.drondea.sms.message.smpp.SmppUnBindRequestMessage;
//import com.drondea.sms.message.smpp.SmppUnBindResponseMessage;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppOutBindMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppSubmitMultiRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppSubmitMultiResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppSubmitSmRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppSubmitSmResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppUnBindResponseMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class submit {
//    public static void main(String[] args) {
//        SmppSubmitMultiResponseMessage message = new SmppSubmitMultiResponseMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_SUBMIT_MULTI_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setMessageId("T123");
//        message.setNoUnsuccess((byte)1);
//        message.setErrorStatusCode(4);
//
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x01 }));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppSubmitMultiResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppSubmitMultiResponseMessage deMessage = null;
//            deMessage = (SmppSubmitMultiResponseMessage) SmppSubmitMultiResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void submitMultiRequestTest(){
//        SmppSubmitMultiRequestMessage message = new SmppSubmitMultiRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_SUBMIT_SM_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setServiceType("asdf");
//        message.setSourceAddrTon((byte)1);
//        message.setSourceAddrNpi((byte)1);
//        message.setSourceAddr("123.123");
//
//        message.setShortMessage("sms".getBytes());
//
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppSubmitMultiRequestMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppSubmitMultiRequestMessage deMessage = null;
//            deMessage = (SmppSubmitMultiRequestMessage) SmppSubmitMultiRequestMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void submitSmResponseTest(){
//        SmppSubmitSmResponseMessage message = new SmppSubmitSmResponseMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_SUBMIT_SM_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setMessageId("110");
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x01 },"testTAG"));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppSubmitSmResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppSubmitSmResponseMessage deMessage = null;
//            deMessage = (SmppSubmitSmResponseMessage) SmppSubmitSmResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void submitRequestTest(){
//        SmppSubmitSmRequestMessage message = new SmppSubmitSmRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_SUBMIT_SM);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(2);
//
//        message.setServiceType("test");
//        message.setSourceAddrTon((byte)1);
//        message.setSourceAddrNpi((byte)1);
//        message.setSourceAddr("123.123");
//        message.setDestAddrTon((byte)1);
//        message.setDestAddrNpi((byte)1);
//        message.setDestinationAddr("321.321");
//        message.setEsmClass((byte)1);
//        message.setProtocolId((byte)1);
//        message.setPriorityFlag((byte)1);
//        message.setScheduleDeliveryTime("1");
//        message.setSmLength((byte)2);
//        message.setShortMessage("ab".getBytes());
//
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_ALERT_ON_MSG_DELIVERY, new byte[] { (byte)0x01 }));
//
//        message.getHeader().setCommandLength(message.getHeaderLength()+message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppSubmitSmRequestMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppSubmitSmRequestMessage deMessage = null;
//            deMessage = (SmppSubmitSmRequestMessage) SmppSubmitSmRequestMessageCodec.decode(byteBuf);
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
