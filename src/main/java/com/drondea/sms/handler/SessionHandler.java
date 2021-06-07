package com.drondea.sms.handler;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.session.SessionManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: session处理器
 * @author: 刘彦宁
 * @date: 2020年06月05日15:36
 **/
public class SessionHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(SessionHandler.class);

    private SessionManager sessionManager;
    private ChannelSession session;

    public SessionHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //创建一个Session对象,用于统计等相关,根据配置类型获取session处理器
        ChannelSession session = sessionManager.createSession(ctx);
        this.session = session;
        sessionManager.addSession(session);
        session.fireChannelActive();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel closed: {}", ctx);
        super.channelInactive(ctx);
        sessionManager.removeSession(session);
        session.fireChannelClosed();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("channel read: {}", msg);
        //优化成线程池处理避免阻塞netty线程
        session.fireMsgReceived((IMessage) msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        logger.debug("netty 触发可写状态改变：{}", ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        session.fireExceptionThrown(cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("write: {}", msg);
        //获取到窗口slot才可以发送
        session.fireWrite(ctx, (IMessage) msg, promise);
    }

}
