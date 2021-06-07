package com.drondea.sms.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.conf.cmpp.CmppClientSocketConfig;
import com.drondea.sms.handler.cmpp.CmppClientCustomHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.session.cmpp.CmppClientSessionManager;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.IMessageResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 中间件主动获取消息模式客户端测试类
 * @author: 刘彦宁
 * @date: 2020年12月15日09:37
 **/
public class CmppClientPullModeTest {

    static AtomicInteger sum = new AtomicInteger();
    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        //滑动窗口建议值为16
        CmppClientSocketConfig socketConfig = new CmppClientSocketConfig("test",
                10 * 1000, 16, host, 7891);
        socketConfig.setChannelSize(1);
        socketConfig.setUserName("100506");
        socketConfig.setPassword("123123");
        socketConfig.setVersion(CmppConstants.VERSION_20);
        //限速 条/s
        socketConfig.setQpsLimit(100);
        //固定签名设置
//        socketConfig.setSignatureDirection(SignatureDirection.CHANNEL_FIXED);
//        socketConfig.setSignaturePosition(SignaturePosition.PREFIX);
//        socketConfig.setSmsSignature("【庄点科技】");
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 1000);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);

        CmppClientCustomHandler cmppCustomHandler = new CmppClientCustomHandler();

        CmppClientSessionManager sessionManager = new CmppClientSessionManager(socketConfig, cmppCustomHandler);
        //注册消息提供者，一般从MQ、缓存、数据库中获取数据
        sessionManager.setMessageProvider(new MessageProvider() {
            @Override
            public List<IMessage> getTcpMessages(ChannelSession channelSession) {
                int i = sum.incrementAndGet();
                if (i > 5) {
                    return null;
                }
                CmppSubmitRequestMessage requestMessage = new CmppSubmitRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceId(sequenceNumber.next());
                String message = i + Math.random() + "第二次李白字太白，号青莲居士，【庄点】";
                requestMessage.setMsgContent(message);
                requestMessage.setServiceId("1");
                requestMessage.setMsgSrc("AAAA");
                requestMessage.setSrcId("" + (int) (Math.random() * 1000));
                requestMessage.setRegisteredDelivery((short) 1);
                requestMessage.setDestUsrTl((short) 1);
                requestMessage.setDestTerminalId(new String[]{"17303110626"});
                requestMessage.setSignature("【庄点科技】");
                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                        System.out.println("收到response:" + request.getSequenceId());
                    }
                    @Override
                    public void messageExpired(String key,IMessage request) {
                        System.out.println("短信超时======" + request.getSequenceId());
                    }
                    @Override
                    public void sendMessageFailed(IMessage request) {
                        System.out.println("短信发送失败:" + request);
                    }
                });
                requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                //长短信拆分
                List<IMessage> longMsgSlices = CommonUtil.getLongMsgSlices(requestMessage, channelSession.getConfiguration(), channelSession.getSequenceNumber());
                return longMsgSlices;
            }

            @Override
            public void responseMessageMatchFailed(String requestKey, IMessage response) {
                System.out.println("上游没有response的情况处理");
            }
        });

        //创建链接
        sessionManager.doOpen();
        sessionManager.doCheckSessions();

        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("已退出!");
    }
}
