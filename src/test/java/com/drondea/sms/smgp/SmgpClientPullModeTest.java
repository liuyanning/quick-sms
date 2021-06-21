package com.drondea.sms.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.conf.cmpp.CmppClientSocketConfig;
import com.drondea.sms.conf.smgp.SmgpClientSocketConfig;
import com.drondea.sms.handler.cmpp.CmppClientCustomHandler;
import com.drondea.sms.handler.smgp.SmgpClientCustomHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.session.cmpp.CmppClientSessionManager;
import com.drondea.sms.session.smgp.SmgpClientSessionManager;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.IMessageResponseHandler;
import com.drondea.sms.type.SmgpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 中间件主动获取消息模式客户端测试类
 * @author: 刘彦宁
 * @date: 2020年12月15日09:37
 **/
public class SmgpClientPullModeTest {

    static AtomicInteger sum = new AtomicInteger();
    public static void main(String[] args) throws InterruptedException {

//        //本地测试
        String host = "127.0.0.1";
        String userName = "100506";
        String password = "w0akYC";
        int port = 8891;

        //滑动窗口建议值为16
        SmgpClientSocketConfig socketConfig = new SmgpClientSocketConfig("test",
                10 * 1000, 16, host, port);
        socketConfig.setChannelSize(1);
        socketConfig.setUserName(userName);
        socketConfig.setPassword(password);
        socketConfig.setVersion(SmgpConstants.DEFAULT_VERSION);

        //限速 条/s
//        socketConfig.setQpsLimit(5000);
        //移除标签
//        socketConfig.setSignatureDirection(SignatureDirection.CHANNEL_FIXED);
//        socketConfig.setSignaturePosition(SignaturePosition.SUFFIX);
//        socketConfig.setSmsSignature("【庄点科技】");
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 1000);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);

        SmgpClientCustomHandler smgpCustomHandler = new SmgpClientCustomHandler();
        SmgpClientSessionManager sessionManager = new SmgpClientSessionManager(socketConfig, smgpCustomHandler);

        sessionManager.setMessageProvider(new MessageProvider() {
            @Override
            public List<IMessage> getTcpMessages(ChannelSession channelSession) {

                int i = sum.incrementAndGet();
                if (i == 1) {
                    System.out.println("当前时间：" + System.currentTimeMillis());
                }

                if (i == 160) {
                    System.out.println("当前时间2：" + System.currentTimeMillis());
                }
                if (i > 170) {
                    return null;
                }

                SmgpSubmitRequestMessage requestMessage = new SmgpSubmitRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceId(sequenceNumber.next());
                String message = i + "您好您的验证码是：长短信您好您的验证码是" + Math.random() + "【庄点科技】";
//                String message = "第条消息哈哈哈哈2" + Math.random();
                requestMessage.setMsgContent(message);
                requestMessage.setServiceId("1");
                requestMessage.setMsgSrc("AAAA");
                requestMessage.setSrcTermId("" + (int) (Math.random() * 1000));
                requestMessage.setNeedReport(true);
                requestMessage.setDestTermIdArray(new String[]{"17303110626"});
                requestMessage.setReserve("1234567");
                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                            System.out.println(" 客户端提交短信 messageComplete ");
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {
                        System.out.println(" 客户端提交短信 messageExpired ");
                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {

                    }
                });
                requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                return CommonUtil.getLongMsgSlices(requestMessage, channelSession.getConfiguration(), channelSession.getSequenceNumber());
            }

            @Override
            public void responseMessageMatchFailed(String requestKey, IMessage response) {

            }
        });

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
