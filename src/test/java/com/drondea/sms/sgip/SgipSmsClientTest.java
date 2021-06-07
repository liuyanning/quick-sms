package com.drondea.sms.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.conf.sgip.SgipClientSocketConfig;
import com.drondea.sms.handler.sgip.SgipClientReportCustomHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipReportRequestMessage;
import com.drondea.sms.session.sgip.SgipClientSessionManager;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.IMessageResponseHandler;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

/**
 * @version V3.0.0
 * @description: 客户端测试
 * @author: liyuehai
 * @date: 2020年06月10日10:37
 **/
public class SgipSmsClientTest {
    public static void main(String[] args) throws InterruptedException {
        GlobalConstants.METRICS_CONSOLE_ON = false;
        String host = "192.168.1.113";
//        String host = "39.107.51.17";
        //滑动窗口建议值为16
        SgipClientSocketConfig socketConfig = new SgipClientSocketConfig("test",
                10 * 1000, 16, host, 5671);
        socketConfig.setChannelSize(1);
        socketConfig.setNodeId(1);
        socketConfig.setUserName("10003");
        socketConfig.setPassword("123123");

        //限速 条/s
//        socketConfig.setQpsLimit(10000);
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 1000);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);

        SgipClientReportCustomHandler sgipCustomHandler = new SgipClientReportCustomHandler();
        SgipClientSessionManager sessionManager = new SgipClientSessionManager(socketConfig, sgipCustomHandler);
        //创建链接
        sessionManager.doOpen();
        //ChannelGroup channels = sessionManager.getChannels();
        List<ChannelSession> sessions = sessionManager.getSessions();

        synchronized (socketConfig) {
            if(sessions.size()==0){
                socketConfig.wait();
            }
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String sgipSequenceNumberNew = scanner.nextLine();
                if (sessions.size()==0) {
                    //创建链接
                    sessionManager.doOpen();
                    socketConfig.wait();
                }
                Channel channel = sessions.get(0).getChannel();

                final EventExecutor executor = channel.pipeline().firstContext().executor();
                new Thread(() -> {
                    int i = 0;
                    if (!sessions.get(0).isWritable()) {
                        System.out.println("不可写");
                    }
                    SgipReportRequestMessage requestMessage = new SgipReportRequestMessage();
                    SequenceNumber sequenceNumber = sessions.get(0).getSequenceNumber();

                    SgipSequenceNumber sgipSequenceNumber = new SgipSequenceNumber(1001, sequenceNumber.next());
                    requestMessage.getHeader().setSequenceNumber(sgipSequenceNumber);
                    String message = "112233" ;
                    requestMessage.setSubmitSequenceNumber(new SgipSequenceNumber(sgipSequenceNumberNew));
                    requestMessage.setReportType((short) 1);
                    requestMessage.setUserNumber("15633094530");
                    requestMessage.setState((short) 0);
                    requestMessage.setErrorCode((short) 0);
                    //收到响应的回调
                    requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                        @Override
                        public void messageComplete(IMessage request, IMessage response) {
                        }
                        @Override
                        public void messageExpired(String key, IMessage request) {
                        }

                        @Override
                        public void sendMessageFailed(IMessage request) {

                        }
                    });
                    executor.submit(() -> {
                        sessions.get(0).sendMessage(requestMessage);
                    });
                }).start();

            }

        }

        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("已退出!");
    }
}
