//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppBindTransceiverRequestMessage;
//import com.drondea.sms.message.smpp.SmppBindTransceiverResponseMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.SmppSubmitSmRequestMessage;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverResponseMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppSubmitSmRequestMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//import org.junit.Test;
//
//public class SmppTest {
//
//    public static void main(String[] args) throws Exception {
//        SmppSubmitSmRequestMessage message = new SmppSubmitSmRequestMessage();
//        message.setHeader(new SmppHeader());
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_SUBMIT_SM);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(0x00000001);
//        message.setDefaultMsgId((byte)1);
//        message.setSmLength((byte)2);
//        message.setShortMessage("ab".getBytes());
////        message.setSystemId("SMPP3TEST");
//
////        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x01 }));
//
//
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//        ByteBuf byteBuf = SmppSubmitSmRequestMessageCodec.encode(message, compBuf);
//        SmppUtil.printPDU(byteBuf);
//
//        SmppSubmitSmRequestMessage deMessage = null;
//        try {
//            deMessage = (SmppSubmitSmRequestMessage) SmppSubmitSmRequestMessageCodec.decode(byteBuf);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(deMessage.toString());
//
//    }
//
//    @Test
//    public void BindTransceiverResponseTest() throws Exception {
//        SmppBindTransceiverResponseMessage message = new SmppBindTransceiverResponseMessage();
//        message.setHeader(new SmppHeader());
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_BIND_TRANSCEIVER_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(0x00000001);
//
//        message.setSystemId("SMPP3TEST");
//
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SOURCE_NETWORK_TYPE, new byte[] { (byte)0x01 }));
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_DEST_NETWORK_TYPE, new byte[] { (byte)0x01 }));
//
//
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//        ByteBuf byteBuf = SmppBindTransceiverResponseMessageCodec.encode(message, compBuf);
//        SmppUtil.printPDU(byteBuf);
//
//        SmppBindTransceiverResponseMessage deMessage = null;
//        try {
//            deMessage = (SmppBindTransceiverResponseMessage) SmppBindTransceiverResponseMessageCodec.decode(byteBuf);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(deMessage.toString());
//    }
//
//    @Test
//    public void BindTransceiverTest() throws Exception {
//        SmppBindTransceiverRequestMessage message = new SmppBindTransceiverRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_BIND_TRANSCEIVER);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(0x00000001);
//
//        message.setSystemId("SMPP3TEST");
//        message.setPassword("secret08");
//        message.setSystemType("SUBMIT1");
//        message.setInterfaceVersion(SmppConstants.VERSION_5_0);
//        message.setAddrTon((byte)0x0);
//        message.setAddrNpi((byte)0x0);
//        message.setAddressRange("123.123");
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//
//
//        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//        ByteBuf byteBuf = SmppBindTransceiverRequestMessageCodec.encode(message, compBuf);
//
//        SmppUtil.printPDU(byteBuf);
//
//        SmppBindTransceiverRequestMessage deMessage = null;
//        try {
//            deMessage = (SmppBindTransceiverRequestMessage) SmppBindTransceiverRequestMessageCodec.decode(byteBuf);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(deMessage.toString());
//    }
//
//
//}
