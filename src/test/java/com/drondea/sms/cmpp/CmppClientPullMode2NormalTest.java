package com.drondea.sms.cmpp;

import com.drondea.sms.conf.cmpp.CmppClientSocketConfig;
import com.drondea.sms.handler.cmpp.CmppClientMessageProvider;
import com.drondea.sms.handler.cmpp.CmppClientProviderCustomHandler;
import com.drondea.sms.session.cmpp.CmppClientSessionManager;
import com.drondea.sms.type.CmppConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 演示中间件中如果将拉取模式转换为主动发送模式
 * 在MessageProvider中定义存储的队列queue即可，发送数据直接放入queue中
 * @version V3.0.0
 * @description: 中间件主动获取消息模式客户端测试类
 * @author: 刘彦宁
 * @date: 2020年12月15日09:37
 **/
public class CmppClientPullMode2NormalTest {

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        //滑动窗口建议值为16
        CmppClientSocketConfig socketConfig = new CmppClientSocketConfig("test",
                10 * 1000, 16, host, 7892);
        socketConfig.setChannelSize(1);
        socketConfig.setUserName("100001");
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

        CmppClientProviderCustomHandler cmppCustomHandler = new CmppClientProviderCustomHandler();

        CmppClientSessionManager sessionManager = new CmppClientSessionManager(socketConfig, cmppCustomHandler);
        //注册消息提供者，一般从MQ、缓存、数据库中获取数据
        sessionManager.setMessageProvider(new CmppClientMessageProvider());

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
