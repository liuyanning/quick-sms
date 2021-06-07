package com.drondea.sms.handler.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.UserChannelConfig;
import com.drondea.sms.windowing.DuplicateKeyException;
import com.drondea.sms.windowing.OfferTimeoutException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @version V3.0.0
 * @description: Smpp的定制处理器
 * @author: gengjinbiao
 * @date: 2020年06月23日17:35
 **/
public class SmppServerCustomHandler extends ICustomHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmppServerCustomHandler.class);

    @Override
    public void fireUserLogin(Channel channel, ChannelSession channelSession) {
        logger.debug("客户端登录了");
    }

    @Override
    public void channelClosed(ChannelSession channelSession) {

    }

    @Override
    public void configPipelineAfterLogin(ChannelPipeline pipeline) {
        pipeline.addLast("ServerMessageRecieverHandler", new ServerSmppSubmitRequestHandler());
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
