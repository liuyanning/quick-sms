package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppSubmitSmRequestMessage;
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
 * @description: smpp的定制处理器
 * @author: gengjinbiao
 * @date: 2020年06月23日17:35
 **/
public class SmppClientCustomHandler extends ICustomHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmppClientCustomHandler.class);

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
                    System.out.println("不可写");
                    //                    continue;
                }
                SmppSubmitSmRequestMessage requestMessage = new SmppSubmitSmRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceNumber(sequenceNumber.next());
//                String message = "AAA";
                String message = "test message:test message:test message:test message:" +
                        "test message:test message:test message:test message:test message:test mes" +
                        "sage:test message:test message:test message:test message:test message:" + Math.random();
                requestMessage.setMsgContent(message, (short) 0);

                requestMessage.setSourceAddrNpi((short) 1);
                requestMessage.setSourceAddrTon((short) 1);
                requestMessage.setSourceAddr("");
//                requestMessage.setDestinationAddr("44951361920"+(int)(Math.random() * 1000));
                requestMessage.setDestAddrNpi((short) 1);
                requestMessage.setDestAddrTon((short) 1);
                requestMessage.setDestinationAddr("8617303110626");
                requestMessage.setProtocolId((short) 0);
                requestMessage.setPriorityFlag((byte) 0x00);
                requestMessage.setScheduleDeliveryTime(null);
                requestMessage.setValidityPeriod(null);
                requestMessage.setRegisteredDelivery((short) 1);
                requestMessage.setReplaceIfPresentFlag((byte) 0x00);
                requestMessage.setDataCoding((byte) 0x00);
                requestMessage.setDefaultMsgId((byte) 0x00);

                requestMessage.getHeader().setCommandLength(requestMessage.getSmLength() + requestMessage.getBodyLength() + requestMessage.getTlvLength());
                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                        System.out.println("完成:" + request.getSequenceId());
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {

                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {

                    }
                });

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
        pipeline.addLast("SmppTestSubmitResponseHandler", new SmppTestSubmitResponseHandler());
        pipeline.addLast("SmppTestDeliveryResponseHandler", new SmppTestDeliveryRequestHandler());
    }

    @Override
    public void responseMessageExpired(Integer sequenceId, IMessage request) {
        System.out.println("短信超时处理" + sequenceId);
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

}
