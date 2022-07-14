package com.drondea.sms.smpp;

import com.drondea.sms.conf.smpp.SmppClientSocketConfig;
import com.drondea.sms.handler.smpp.SmppClientCustomHandler;
import com.drondea.sms.session.smpp.SmppClientSessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version V3.0.0
 * @description: 客户端测试
 * @author: gengjinbiao
 * @date: 2020年07月10日10:37
 **/
public class SmppClientTest {
    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        //滑动窗口建议值为16
        SmppClientSocketConfig socketConfig = new SmppClientSocketConfig("bind1",
                30 * 1000, 16, host, 7889);
        socketConfig.setChannelSize(1);
        socketConfig.setSystemId("100003");
        socketConfig.setPassword("123123");
//        socketConfig.setPassword("100001");
//        socketConfig.setVersion(SmppConstants.VERSION_3_4);

        //限速 条/s
        socketConfig.setQpsLimit(100);
        //移除标签
//        socketConfig.setSignatureDirection(SignatureDirection.CHANNEL_FIXED);
//        socketConfig.setSignaturePosition(SignaturePosition.SUFFIX);
//        socketConfig.setSmsSignature("【庄点科技】");
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 10);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);

        SmppClientCustomHandler smppCustomHandler = new SmppClientCustomHandler();
        SmppClientSessionManager sessionManager = new SmppClientSessionManager(socketConfig, smppCustomHandler);
        sessionManager.doOpen();
        sessionManager.doCheckSessions();

        //添加业务handler
//        sessionManager.setPipelineManager( pipeline -> {
//            pipeline.addLast("CmppTestSubmitResponseHandler", new CmppTestSubmitResponseHandler());
//            pipeline.addLast("CmppTestDeliveryRequestHandler", new CmppTestDeliveryRequestHandler());
//        });

//        Thread.sleep(5000);
//        ExecutorService cachedExecutor = DefaultEventGroupFactory.getInstance().getCachedExecutor();
//        int activeThread = ((ThreadPoolExecutor)cachedExecutor).getActiveCount();
//        System.out.println("active thread：" + activeThread);
        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("已退出!");
    }
}
