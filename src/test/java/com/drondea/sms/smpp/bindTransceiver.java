//package com.drondea.sms.smppTest;
//
//import com.drondea.sms.common.util.SmppUtil;
//import com.drondea.sms.message.smpp.SmppBindTransceiverRequestMessage;
//import com.drondea.sms.message.smpp.SmppBindTransceiverResponseMessage;
//import com.drondea.sms.message.smpp.SmppHeader;
//import com.drondea.sms.message.smpp.Tlv;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverRequestMessageCodec;
//import com.drondea.sms.message.smpp.codec.SmppBindTransceiverResponseMessageCodec;
//import com.drondea.sms.type.SmppConstants;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.CompositeByteBuf;
//import io.netty.buffer.Unpooled;
//
//public class bindTransceiver {
//    public static void main(String[] args) {
//        SmppBindTransceiverResponseMessage message = new SmppBindTransceiverResponseMessage();
//        message.setHeader(new SmppHeader());
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_BIND_TRANSCEIVER_RESP);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(0x00000001);
//        message.setSystemId("SMPP3TEST");
//        message.addOptionalParameter(new Tlv(SmppConstants.TAG_SC_INTERFACE_VERSION, new byte[] { SmppConstants.VERSION_5_0 }));
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppBindTransceiverResponseMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppBindTransceiverResponseMessage deMessage = null;
//            deMessage = (SmppBindTransceiverResponseMessage) SmppBindTransceiverResponseMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void testRequest(){
//        SmppBindTransceiverRequestMessage message = new SmppBindTransceiverRequestMessage();
//        message.setHeader(new SmppHeader());
//
//        message.getHeader().setCommandId(SmppConstants.CMD_ID_BIND_TRANSCEIVER);
//        message.getHeader().setCommandStatus(SmppConstants.STATUS_OK);
//        message.getHeader().setSequenceNumber(0x00000001);
//
//        message.setSystemId("SMPP3TEST");
//        message.setPassword("password123");
//        message.setSystemType("bindTransceiver");
//        message.setInterfaceVersion(SmppConstants.VERSION_5_0);
//        message.setAddrTon((byte)0x0);
//        message.setAddrNpi((byte)0x0);
//        message.setAddressRange("123.123");
//
//        message.getHeader().setCommandLength(message.getHeaderLength() + message.getBodyLength());
//
//
//        try {
//            CompositeByteBuf compBuf = Unpooled.compositeBuffer();
//            ByteBuf byteBuf = SmppBindTransceiverRequestMessageCodec.encode(message, compBuf);
//
//            SmppUtil.printPDU(byteBuf);
//
//            SmppBindTransceiverRequestMessage deMessage = null;
//            deMessage = (SmppBindTransceiverRequestMessage) SmppBindTransceiverRequestMessageCodec.decode(byteBuf);
//            System.out.println(deMessage.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
