package com.drondea.sms.windowing;

import com.codahale.metrics.Timer;
import com.drondea.sms.message.IMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @version V3.0.0
 * @description: netty的消息封装
 * @author: 刘彦宁
 * @date: 2020年07月03日09:51
 **/
public class ChannelWindowMessage {

    private ChannelHandlerContext ctx;
    private IMessage message;
    private ChannelPromise promise;
    private Timer.Context timeContext;

    public ChannelWindowMessage(ChannelHandlerContext ctx, IMessage message,
                                ChannelPromise promise) {
        this.ctx = ctx;
        this.message = message;
        this.promise = promise;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public IMessage getMessage() {
        return message;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public ChannelPromise getPromise() {
        return promise;
    }

    public void setPromise(ChannelPromise promise) {
        this.promise = promise;
    }

    public Timer.Context getTimeContext() {
        return timeContext;
    }

    public void setTimeContext(Timer.Context timeContext) {
        this.timeContext = timeContext;
    }
}
