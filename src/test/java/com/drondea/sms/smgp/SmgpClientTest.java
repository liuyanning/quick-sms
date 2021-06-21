package com.drondea.sms.smgp;

import com.drondea.sms.conf.smgp.SmgpClientSocketConfig;
import com.drondea.sms.handler.smgp.SmgpClientCustomHandler;
import com.drondea.sms.session.smgp.SmgpClientSessionManager;
import com.drondea.sms.type.SmgpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0
 * @description: smgp客户端测试
 * @author: ywj
 * @date: 2020年06月10日10:37
 **/
public class SmgpClientTest {
    public static void main(String[] args) throws InterruptedException {
//        GlobalConstants.METRICS_CONSOLE_ON = true;
//        GlobalConstants.METRICS_ON = true;


//        //本地测试
        String host = "127.0.0.1";
        String userName = "100003";
        String password = "123123";
        int port = 8891;

        //线上 智明通讯(电信) 地址
//        String host = "61.129.57.39";
//        String userName = "6094";
//        String password = "6094";
//        int port = 8080;

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
