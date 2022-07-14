package com.drondea.sms.sgip;

import com.drondea.sms.conf.sgip.SgipServerSocketConfig;
import com.drondea.sms.handler.sgip.SgipServerCustomHandler;
import com.drondea.sms.session.sgip.SgipServerSessionManager;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.UserChannelConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0.0
 * @description: 服务器端简单测试
 * @author: liyuehai
 * @date: 2020年06月10日18:48
 **/
public class SgipServerTest {

    public static void main(String[] args) {
        GlobalConstants.METRICS_CONSOLE_ON = false;
        SgipServerSocketConfig socketConfig = new SgipServerSocketConfig("test", 8802);
        socketConfig.setIdleTime(60);
        SgipServerCustomHandler customHandler = new SgipServerCustomHandler();

        SgipServerSessionManager sessionManager = new SgipServerSessionManager(name -> {
            if (name.startsWith("100001")) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setPassword("123123");
                userChannelConfig.setChannelLimit(3);
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
