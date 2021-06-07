package com.drondea.sms.handler.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipReportRequestMessage;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.IMessageResponseHandler;
import com.drondea.sms.type.UserChannelConfig;
import com.drondea.sms.windowing.DuplicateKeyException;
import com.drondea.sms.windowing.OfferTimeoutException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @version V3.0.0
 * @description: sgip的定制处理器
 * @author: liyuehai
 * @date: 2020年06月23日17:35
 **/
public class SgipClientReportCustomHandler extends ICustomHandler {

    private static final Logger logger = LoggerFactory.getLogger(SgipClientReportCustomHandler.class);

    @Override
    public void fireUserLogin(Channel channel, ChannelSession channelSession) {

        //注册session可写状态监听器
        channelSession.setSessionEventHandler((writable) -> {
            System.out.println("可写状态发生改变：" + writable);
        });

        final EventExecutor executor = channel.pipeline().firstContext().executor();


        System.out.println("用户登录成功--开始发送短信");

        //todo 怎么判断消息繁忙状态，不可写状态
        new Thread(() -> {

            int i = 0;
            while (true) {

                if (!channelSession.isWritable()) {
                    //                    System.out.println("不可写");
                    //                    continue;
                }
                SgipSequenceNumber submitSequenceNumber = new SgipSequenceNumber(11, 22, 33);

                SgipReportRequestMessage requestMessage = new SgipReportRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                SgipSequenceNumber sgipSequenceNumber = new SgipSequenceNumber(1001, sequenceNumber.next());
                requestMessage.getHeader().setSequenceNumber(sgipSequenceNumber);
                requestMessage.setSubmitSequenceNumber(submitSequenceNumber);

                requestMessage.setReportType((short) 1);
                requestMessage.setUserNumber("17332958317");
                requestMessage.setState((short) 0);
                requestMessage.setErrorCode((short) 0);

                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
//                        System.out.println("完成:" + request.getSequenceId());
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {

                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {

                    }
                });
//                if(i % 64 == 0) {
//                    try {
//                        Thread.sleep(2, 0);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                executor.submit(() -> {
                    channelSession.sendMessage(requestMessage);
                });
                //                channel.writeAndFlush(requestMessage);
                i++;
                if (i == 1) {
                    break;
                }
            }
        }).start();
    }

    @Override
    public void channelClosed(ChannelSession channelSession) {

    }

    @Override
    public void configPipelineAfterLogin(ChannelPipeline pipeline) {
        pipeline.addLast("SgipClientReportResponseHandler", new SgipClientReportResponseHandler());
    }

    @Override
    public void responseMessageExpired(Integer sequenceId, IMessage request) {
        System.out.println("Report短信超时处理" + sequenceId);
    }

    @Override
    public void slidingWindowException(ChannelSession session, ChannelHandlerContext ctx, IMessage message, ChannelPromise promise, Exception exception) {
        logger.error("slidingWindowException", exception);
        int retryCount = message.addRetryCount();
        //失败越多延时越长，防止线程堆积
        int delay = 10;
        if (retryCount >= 30) {
            delay = 500 * retryCount / 10;
        }
        if (retryCount > 20) {
            System.out.println("重试" + retryCount);
        }
        //重写
        ctx.executor().schedule(() -> {
            session.sendWindowMessage(ctx, message, promise);
        }, delay, TimeUnit.MILLISECONDS);
        //滑动窗口key冲突
        if (exception instanceof DuplicateKeyException) {
            return;
        }
        //滑动窗口获取slot超时
        if (exception instanceof OfferTimeoutException) {
            return;
        }
        if (exception instanceof InterruptedException) {
            return;
        }
    }

    @Override
    public boolean customLoginValidate(IMessage message, UserChannelConfig channelConfig, Channel channel) {
        return true;
    }

    @Override
    public void failedLogin(ChannelSession channelSession, IMessage msg, long status) {

    }

}
