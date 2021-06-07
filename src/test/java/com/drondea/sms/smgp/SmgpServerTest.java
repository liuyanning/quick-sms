package com.drondea.sms.smgp;

import com.drondea.sms.conf.smgp.SmgpServerSocketConfig;
import com.drondea.sms.handler.smgp.SmgpServerCustomHandler;
import com.drondea.sms.session.smgp.SmgpServerSessionManager;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.UserChannelConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0
 * @description: 服务器端简单测试
 * @author: ywj
 * @date: 2020年06月10日18:48
 **/
public class SmgpServerTest {

    public static void main(String[] args) {
        GlobalConstants.METRICS_CONSOLE_ON = true;
//        GlobalConstants.METRICS_ON = true;
        SmgpServerSocketConfig socketConfig = new SmgpServerSocketConfig("test", 8891);

        SmgpServerCustomHandler serverCustomHandler = new SmgpServerCustomHandler();
        SmgpServerSessionManager sessionManager = new SmgpServerSessionManager(name -> {
            //todo 这里的代码应该用个接口之类的去获取用户信息，封装 UserChannelConfig
            if ("100003".equals(name)) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setPassword("123123");
                userChannelConfig.setQpsLimit(100);
                return userChannelConfig;
            }
            return null;
        }, socketConfig, serverCustomHandler);

        sessionManager.doOpen();

        System.out.println("服务器监听已经启动");
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
