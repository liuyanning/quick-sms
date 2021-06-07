package com.drondea.sms.smpp;

import com.drondea.sms.conf.smpp.SmppServerSocketConfig;
import com.drondea.sms.handler.smpp.SmppServerCustomHandler;
import com.drondea.sms.session.smpp.SmppServerSessionManager;
import com.drondea.sms.type.UserChannelConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0.0
 * @description: 服务器端简单测试
 * @author: gengjinbiao
 * @date: 2020年06月10日18:48
 **/
public class SmppServerTest {

    public static void main(String[] args) {
        SmppServerSocketConfig socketConfig = new SmppServerSocketConfig("test", 7889);

        SmppServerCustomHandler customHandler = new SmppServerCustomHandler();

        SmppServerSessionManager sessionManager = new SmppServerSessionManager(name -> {
            if (name.startsWith("100003")) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setPassword("123123");
//                userChannelConfig.setQpsLimit(5000);
                return userChannelConfig;
            }
            return null;
        }, socketConfig, customHandler);
        sessionManager.doOpen();


        System.out.println("服务器监听已经启动");
        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
