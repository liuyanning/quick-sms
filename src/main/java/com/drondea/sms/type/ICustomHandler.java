package com.drondea.sms.type;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.message.IMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;

/**
 * 自定义的事件和定制处理
 *
 * @author liuyanning
 */
public abstract class ICustomHandler {

    /**
     * 用户登录事件的触发
     *
     * @param channel
     * @param channelSession
     */
    public abstract void fireUserLogin(Channel channel, ChannelSession channelSession);

    /**
     * channel关闭时候触发
     *
     * @param channelSession
     */
    public abstract void channelClosed(ChannelSession channelSession);

    /**
     * 用户登录后，可以改变pipeline的设置，如增加业务handler
     *
     * @param pipeline
     */
    public abstract void configPipelineAfterLogin(ChannelPipeline pipeline);

    /**
     * 滑动窗口短信提交响应超时处理
     *
     * @param sequenceId
     * @param request
     */
    public abstract void responseMessageExpired(Integer sequenceId, IMessage request);


    /**
     * 滑动窗口的异常处理,集中超时异常
     *
     * @param session
     * @param ctx
     * @param message
     * @param promise
     * @param exception
     */
    public abstract void slidingWindowException(ChannelSession session, ChannelHandlerContext ctx, IMessage message,
                                                ChannelPromise promise, Exception exception);


    /**
     * 额外的服务器端登录时自定义验证方法,客户端用于验证登录返回结果
     *
     * @param message
     * @param channelConfig
     * @return
     */
    public abstract boolean customLoginValidate(IMessage message, UserChannelConfig channelConfig, Channel channel);


    /**
     * 服务器端用户登录失败验证
     *
     * @param channelSession
     * @param msg
     * @param status
     */
    public void failedLogin(ChannelSession channelSession, IMessage msg, long status) {

    }
}
