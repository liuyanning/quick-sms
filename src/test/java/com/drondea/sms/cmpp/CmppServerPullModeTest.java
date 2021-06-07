package com.drondea.sms.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.conf.cmpp.CmppServerSocketConfig;
import com.drondea.sms.handler.cmpp.CmppServerCustomHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.session.cmpp.CmppServerSessionManager;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.IMessageResponseHandler;
import com.drondea.sms.type.UserChannelConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: cmpp服务器端拉取模式测试
 * @author: 刘彦宁
 * @date: 2020年12月15日16:42
 **/
public class CmppServerPullModeTest {

    static AtomicInteger sum = new AtomicInteger();

    public static void main(String[] args) {
        //监听端口
        CmppServerSocketConfig socketConfig = new CmppServerSocketConfig("test", 7891);
        CmppServerCustomHandler customHandler = new CmppServerCustomHandler();
        //服务器端默认版本号2.0
        socketConfig.setVersion(CmppConstants.VERSION_20);
        CmppServerSessionManager sessionManager = new CmppServerSessionManager(name -> {
            //用户认证逻辑
            if (name.startsWith("100003")) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setId(name);
                userChannelConfig.setWindowSize(32);
                userChannelConfig.setPassword("123123");
                userChannelConfig.setQpsLimit(5000);
                return userChannelConfig;
            }
            return null;
        }, socketConfig, customHandler);

        //设置消息提供者（从数据库或者缓存获取消息即可），这里获取的消息是回执或者上行短信
        sessionManager.setMessageProvider(new MessageProvider() {
            @Override
            public List<IMessage> getTcpMessages(ChannelSession channelSession) {

                int i = sum.incrementAndGet();
                if (i > 2) {
                    return null;
                }

                CmppDeliverRequestMessage mo = new CmppDeliverRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                mo.getHeader().setSequenceId(sequenceNumber.next());
                mo.setRegisteredDelivery((short) 0);
                mo.setMsgContent("TEST");
                mo.setDestId("18010181663");
                //收到响应的回调
                mo.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                        System.out.println("完成:" + request.getSequenceId());
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {
                        System.out.println("短信超时======" + request.getSequenceId());
                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {
                        System.out.println("send failure:" + request);
                    }
                });
                mo.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                return CommonUtil.getLongMsgSlices(mo, channelSession.getConfiguration(), sequenceNumber);
            }

            @Override
            public void responseMessageMatchFailed(String requestKey, IMessage response) {

            }
        });
        sessionManager.doOpen();

        System.out.println("服务器监听已经启动");
        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
