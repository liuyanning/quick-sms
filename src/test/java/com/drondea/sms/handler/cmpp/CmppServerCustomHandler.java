package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.type.ICustomHandler;
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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @version V3.0.0
 * @description: cmpp的定制处理器
 * @author: 刘彦宁
 * @date: 2020年06月23日17:35
 **/
public class CmppServerCustomHandler extends ICustomHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmppServerCustomHandler.class);

    @Override
    public void fireUserLogin(Channel channel, ChannelSession channelSession) {

        logger.debug("客户端事件处理");

        AbstractServerSession serverSession = (AbstractServerSession) channelSession;
        UserChannelConfig userChannelConfig = serverSession.getUserChannelConfig();
        countConnection((AbstractServerSessionManager) serverSession.getSessionManager(), userChannelConfig.getUserName());

    }

    private void countConnection(AbstractServerSessionManager abstractServerSessionManager, String tcpUserName) {
        if (abstractServerSessionManager == null) {
            return;
        }
        List<ChannelSession> userSessions = abstractServerSessionManager.getUserSessions(tcpUserName);
        if (userSessions == null) {
            return;
        }

        Map<String, Long> userIpCounting = userSessions.stream().collect(Collectors.groupingBy(userSession -> {
            Channel channel = userSession.getChannel();
            if (channel == null) {
                return "unknown";
            }
            return ((InetSocketAddress) channel.remoteAddress())
                    .getAddress().getHostAddress();
        }, Collectors.counting()));
        System.out.println(userIpCounting);
    }

    @Override
    public void channelClosed(ChannelSession channelSession) {
        System.out.println("关闭连接");
    }

    @Override
    public void configPipelineAfterLogin(ChannelPipeline pipeline) {
        pipeline.addLast("ServerMessageRecieverHandler", new ServerCmppSubmitRequestHandler());
    }

    @Override
    public void responseMessageExpired(Integer sequenceId, IMessage request) {
        System.out.println("短信超时处理" + sequenceId);
    }

    @Override
    public void slidingWindowException(ChannelSession session, ChannelHandlerContext ctx, IMessage message, ChannelPromise promise, Exception exception) {
        logger.error("slidingWindowException", exception);
    }

    @Override
    public boolean customLoginValidate(IMessage message, UserChannelConfig channelConfig, Channel channel) {
        return true;
    }

    @Override
    public void failedLogin(ChannelSession channelSession, IMessage msg, long status) {

    }

}
