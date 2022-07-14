package com.drondea.sms.sgip;

import com.drondea.sms.conf.sgip.SgipClientSocketConfig;
import com.drondea.sms.handler.sgip.SgipClientCustomHandler;
import com.drondea.sms.session.sgip.SgipClientSessionManager;
import com.drondea.sms.type.GlobalConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0.0
 * @description: 客户端测试
 * @author: liyuehai
 * @date: 2020年06月10日10:37
 **/
public class SgipClientTest {
    public static void main(String[] args) throws InterruptedException {
        GlobalConstants.METRICS_CONSOLE_ON = false;
        String host = "127.0.0.1";
        //滑动窗口建议值为16
        SgipClientSocketConfig socketConfig = new SgipClientSocketConfig("test",
                10 * 1000, 16, host, 8802);
        socketConfig.setChannelSize(1);
        socketConfig.setNodeId(0);
        //socketConfig.setLoginType((short) 2);
        socketConfig.setUserName("100001");
        socketConfig.setPassword("123123");

        //限速 条/s
        socketConfig.setQpsLimit(10);
        //移除标签
//        socketConfig.setSignatureDirection(SignatureDirection.CHANNEL_FIXED);
//        socketConfig.setSignaturePosition(SignaturePosition.SUFFIX);
//        socketConfig.setSmsSignature("【庄点科技】");
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 1000);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);

        SgipClientCustomHandler sgipCustomHandler = new SgipClientCustomHandler();
        SgipClientSessionManager sessionManager = new SgipClientSessionManager(socketConfig, sgipCustomHandler);
        //创建链接
        sessionManager.doOpen();
//        sessionManager.doCheckSessions();
        Thread.sleep(60 * 1000);
        System.out.println("再次连接");
//        sessionManager.doOpen();
        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("已退出!");
    }
}
