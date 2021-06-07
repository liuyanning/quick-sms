package com.drondea.sms.smgp;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.common.util.SystemClock;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.codec.SmgpConnectRequestMessageCodec;
import com.drondea.sms.message.smgp30.codec.SmgpConnectResponseMessageCodec;
import com.drondea.sms.message.smgp30.codec.SmgpSubmitRequestMessageCodec;
import com.drondea.sms.message.smgp30.msg.SmgpConnectRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpConnectResponseMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.IMessageResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;

/**
 * @author ywj
 * @version 3.0
 * @date 2020.07.07 09.19
 */
public class SmgpCodecTest {

    public static void main(String[] args) throws Exception {

        testSmgpSubmitRequestMessageCodec();//提交短信
//        testSmgpConnectResponseMessageCodec();//连接响应
//        testSmgpConnectRequestMessageCodec();//连接请求

    }

    //测试连接响应
    private static void testSmgpSubmitRequestMessageCodec() throws Exception {
        SmgpSubmitRequestMessageCodec codec = new SmgpSubmitRequestMessageCodec();
        SmgpSubmitRequestMessage requestMessage = new SmgpSubmitRequestMessage();
        String content = "第asdfasdfasd";
        requestMessage.setMsgContentBytes(new byte[requestMessage.getMsgLength()]);
        requestMessage.setMsgContent(content);
        requestMessage.setServiceId("1");
        requestMessage.setMsgSrc("AAAA");
        requestMessage.setSrcTermId("" + (int) (Math.random() * 1000));
        requestMessage.setNeedReport(true);
        requestMessage.setDestTermIdArray(new String[]{"17332958317"});

        requestMessage.setMsgSrc("setMsgSrc");
        requestMessage.setTpUdhi((byte) 11);
        requestMessage.setLinkId("setLinkId");
        requestMessage.setReserve("setReserve");
        requestMessage.setAtTime("11223333");
        requestMessage.setMServiceId("setMServiceId");
        requestMessage.setTpPid((byte) 1);


        //收到响应的回调
        requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
            @Override
            public void messageComplete(IMessage request, IMessage response) {
                System.out.println("完成:" + request.getSequenceId());
            }

            @Override
            public void messageExpired(String key, IMessage request) {
                System.out.println("完成: messageExpired" + request.getSequenceId());
            }

            @Override
            public void sendMessageFailed(IMessage request) {

            }
        });
        requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));


        ByteBuf encode = codec.encode(requestMessage, Unpooled.buffer());//编码

        SmppUtil.printPDU(encode);//打印编码
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(encode.array());//复制一份buffer
        System.out.println("================分界线=============分界线============");

        //解码
        SmgpHeader header = new SmgpHeader(0x80000001);
        SmgpSubmitRequestMessage decodeMsg = (SmgpSubmitRequestMessage) codec.decode(header, copiedBuffer);

        System.out.println(decodeMsg.getSequenceNum());
        byte tpUdhi = decodeMsg.getTpUdhi();
        String msgSrc = decodeMsg.getMsgSrc();
        System.out.println(tpUdhi);
        System.out.println(msgSrc);
    }

    //测试连接响应
    private static void testSmgpConnectResponseMessageCodec() {
        SmgpConnectResponseMessageCodec codec = new SmgpConnectResponseMessageCodec();
        SmgpConnectResponseMessage msg = new SmgpConnectResponseMessage();
        msg.setStatus(1);
        msg.setAuthenticatorServer(new byte[16]);
        msg.setServerVersion((short) 0x30);

        ByteBuf encode = codec.encode(msg, Unpooled.buffer());//编码

//        TestUtils.printPDU(encode);//打印编码
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(encode.array());//复制一份buffer
        System.out.println("================分界线=============分界线============");

        //解码
        SmgpHeader header = new SmgpHeader(39, 0x80000001, 2222);
        SmgpConnectResponseMessage decodeMsg = (SmgpConnectResponseMessage) codec.decode(header, copiedBuffer);

        System.out.println(decodeMsg.getStatus());
        System.out.println(Arrays.toString(decodeMsg.getAuthenticatorServer()));
        System.out.println(decodeMsg.getServerVersion());
    }

    //测试连接请求
    private static void testSmgpConnectRequestMessageCodec() {
        SmgpConnectRequestMessageCodec codec = new SmgpConnectRequestMessageCodec();
        SmgpConnectRequestMessage msg = new SmgpConnectRequestMessage();
        msg.setClientId("12345678");
        msg.setLoginMode((byte) 2);
        long timeStamp = Long.parseLong(DateFormatUtils.format(SystemClock.now(), "MMddHHmmss"));
        msg.setTimestamp(timeStamp);
        byte[] source = CommonUtil.getAuthenticatorSource("admin", "123123", timeStamp + "", CharsetUtil.UTF_8);
        System.out.println(Arrays.toString(source));
        msg.setAuthenticatorClient(source);
        msg.setClientVersion((short) 0x30);

        ByteBuf encode = codec.encode(msg, Unpooled.buffer());//编码

//        TestUtils.printPDU(encode);//打印编码
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(encode.array());//复制一份buffer
        System.out.println("================分界线=============分界线============");

        //解码
        SmgpHeader header = new SmgpHeader(39, 0x00000001, 2222);
        SmgpConnectRequestMessage decodeMsg = (SmgpConnectRequestMessage) codec.decode(header, copiedBuffer);

        System.out.println(decodeMsg.getClientId());
        System.out.println(Arrays.toString(decodeMsg.getAuthenticatorClient()));
        System.out.println(decodeMsg.getLoginMode());
        System.out.println(decodeMsg.getTimestamp());
        System.out.println(decodeMsg.getClientVersion());
    }
}
