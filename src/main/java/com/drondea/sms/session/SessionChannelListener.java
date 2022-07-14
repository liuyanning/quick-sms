package com.drondea.sms.session;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.type.ISessionEventHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.function.Function;

/**
 * @author liuyanning
 */
public interface SessionChannelListener {

    /**
     * channel 激活触发
     */
    void fireChannelActive();

    /**
     * 收到消息触发.
     *
     * @param message The message decoded from the Channel
     */
    void fireMsgReceived(IMessage message);

    /**
     * 发生异常触发
     *
     * @param t The exception thrown
     */
    void fireExceptionThrown(Throwable t);

    /**
     * channel关闭触发 (reached EOF or timed out)
     */
    void fireChannelClosed();


    /**
     * 设置session的监听器
     *
     * @param sessionEventHandler
     */
    void setSessionEventHandler(ISessionEventHandler sessionEventHandler);

}
