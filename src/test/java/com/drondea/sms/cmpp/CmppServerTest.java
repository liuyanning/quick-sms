package com.drondea.sms.cmpp;

import com.drondea.sms.conf.cmpp.CmppServerSocketConfig;
import com.drondea.sms.handler.cmpp.CmppServerCustomHandler;
import com.drondea.sms.session.cmpp.CmppServerSessionManager;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.UserChannelConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0.0
 * @description: 服务器端简单测试
 * @author: 刘彦宁
 * @date: 2020年06月10日18:48
 **/
public class CmppServerTest {

    public static void main(String[] args) {
//        GlobalConstants.METRICS_CONSOLE_ON = true;
        CmppServerSocketConfig socketConfig = new CmppServerSocketConfig("test", 7892);

        CmppServerCustomHandler customHandler = new CmppServerCustomHandler();
        //服务器端默认版本号2.0
        socketConfig.setVersion(CmppConstants.VERSION_20);
        CmppServerSessionManager sessionManager = new CmppServerSessionManager(name -> {

            if (name.startsWith("100001")) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setId(name);
                userChannelConfig.setPassword("123123");
//                userChannelConfig.setIdleTime(5);
                userChannelConfig.setChannelLimit(5);
                userChannelConfig.setWindowSize(10000);
//                userChannelConfig.setQpsLimit(100);
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
