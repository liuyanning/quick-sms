package com.drondea.sms.channel;

import com.codahale.metrics.Timer;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.SendFailMessage;
import com.drondea.sms.session.SessionChannelListener;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.DefaultEventGroupFactory;
import com.drondea.sms.type.DelayResponseCache;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.windowing.ChannelWindowMessage;
import com.drondea.sms.windowing.WaitingMessage;
import com.drondea.sms.windowing.Window;
import com.drondea.sms.windowing.WindowFuture;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: 客户端和服务器端公用的session属性和方法
 * @author: 刘彦宁
 * @date: 2020年06月10日16:09
 **/
public abstract class ChannelSession implements SessionChannelListener {

    private static final Logger logger = LoggerFactory.getLogger(ChannelSession.class);

    /**
     * session初始状态
     */
    static public final int STATE_INITIAL = 0;
    /**
     * Session active
     */
    static public final int STATE_ACTIVE = 1;
    /**
     * Session 正在登录
     */
    static public final int STATE_LOGINING = 2;
    /**
     * Session 已经登录，可以发送数据请求
     */
    static public final int STATE_LOGIN_SUCCESS = 3;
    /**
     * Session  退出登录
     */
    static public final int STATE_UNLOGIN = 4;
    /**
     * Session 销毁 closed (destroyed)
     */
    static public final int STATE_CLOSED = 5;
    /**
     * Descriptions of each state
     */
    static public final String[] STATES = {
            "INITIAL", "ACTIVE", "LOGINING", "LOGIN_SUCCESS", "UNLOGIN", "CLOSED"
    };

    /**
     * Gets the configuration associated with this session.
     *
     * @return The session configuration
     */
    public abstract SocketConfig getConfiguration();

    /**
     * Gets the name of the current state of the session.
     *
     * @return The current state of the session by name such as "CLOSED"
     */
    public abstract String getStateName();

    /**
     * 获取session状态
     *
     * @return
     */
    public abstract int getState();

    /**
     * Gets the interface version currently in use between local and remote
     * endpoints.  This interface version is negotiated during the bind process
     * to mainly ensure that optional parameters are supported.
     *
     * @return The interface version currently in use between local and remote
     * endpoints.
     */
    public abstract byte getInterfaceVersion();

    /**
     * Returns whether optional parameters are supported with the remote
     * endpoint.  If the interface version currently in use is >= 3.4, then
     * this method returns true, otherwise will return false.
     *
     * @return True if optional parameters are supported, otherwise false.
     */
    public abstract boolean areOptionalParametersSupported();


    /**
     * Checks if the session is currently in the "OPEN" state.  The "OPEN" state
     * means the session is connected and a bind is pending.
     *
     * @return True if session is currently in the "OPEN" state, otherwise false.
     */
    public abstract boolean isOpen();

    /**
     * Checks if the session is currently in the "BINDING" state.  The "BINDING" state
     * means the session is in the process of binding.  If local is ESME, we sent
     * the bind request, but have not yet received the bind response.  If the local
     * is SMSC, then the ESME initiated a bind request, but we have't responded yet.
     *
     * @return True if session is currently in the "BINDING" state, otherwise false.
     */
    public abstract boolean isBinding();

    /**
     * Checks if the session is currently in the "BOUND" state.  The "BOUND" state
     * means the session is bound and ready to process requests.
     *
     * @return True if session is currently in the "BOUND" state, otherwise false.
     */
    public abstract boolean isBound();

    /**
     * Checks if the session is currently in the "UNBINDING" state.  The "UNBINDING" state
     * means the session is in the process of unbinding. This may have been initiated
     * by us or them.
     *
     * @return True if session is currently in the "UNBINDING" state, otherwise false.
     */
    public abstract boolean isUnbinding();

    /**
     * Checks if the session is currently in the "CLOSED" state.  The "CLOSED" state
     * means the session is unbound and closed (destroyed).
     *
     * @return True if session is currently in the "CLOSED" state, otherwise false.
     */
    public abstract boolean isClosed();


    /**
     * 关闭channel
     */
    public abstract void close();

    /**
     * 获取关联的channel
     *
     * @return
     */
    public abstract Channel getChannel();


    /**
     * 获取session的计数器
     *
     * @return
     */
    public abstract IChannelSessionCounters getCounters();


    /**
     * 记录发送数据，做统计使用
     *
     * @param message
     */
    public abstract void countTXMessage(IMessage message);


    /**
     * 记录接受数据，做统计使用
     *
     * @param message
     */
    public abstract void countRXMessage(IMessage message);

    /**
     * 发送数据
     *
     * @param message
     * @return
     */
    public abstract ChannelFuture sendMessage(IMessage message);

//    /**
//     * 使用滑动窗口发送消息
//     * @param message
//     * @param offerTimeOut 滑动窗口获取slot超时时间，毫秒
//     * @return
//     * @throws DuplicateKeyException
//     * @throws InterruptedException
//     * @throws OfferTimeoutException
//     */
//    public abstract ChannelFuture sendWindowMessage(IMessage message,int offerTimeOut) throws DuplicateKeyException, InterruptedException, OfferTimeoutException;

    /**
     * 获取session的sequence生成器
     *
     * @return
     */
    public abstract SequenceNumber getSequenceNumber();

    /**
     * 获取session管理器
     *
     * @return
     */
    public abstract SessionManager getSessionManager();


    /**
     * 获取滑动窗口
     *
     * @return
     */
    public abstract Window<Integer, ChannelWindowMessage, IMessage> getSlidingWindow();


    /**
     * 滑动窗口发送消息
     *
     * @param ctx
     * @param message
     * @param promise
     */
    public abstract void sendWindowMessage(ChannelHandlerContext ctx, IMessage message, ChannelPromise promise);

    /**
     * 判断session是否可写
     *
     * @return
     */
    public abstract boolean isWritable();

    /**
     * 获取限速等待数量
     * @return
     */
    public abstract int getWaitSize();

    /**
     * 登陆成功后调用
     * @param channel
     */
    protected abstract void notifyChannelLoginSuccess(Channel channel);


    public void delayPullWindowMsg(int delay){
        DefaultEventGroupFactory.getInstance().getPullScheduleExecutor().schedule(() -> {
            try {
                pullAndSendWindowMsgs();
            } catch (Exception e) {
                logger.error("pull message exception:", e);
                //报错情况下延时1秒重试
                delayPullWindowMsg(1000);
            }

        }, delay, TimeUnit.MILLISECONDS);
    }

    protected abstract void pullAndSendWindowMsgs();

    /**
     * 是否需要重发
     * @return
     */
    protected abstract boolean needSendLater(IMessage request, IMessage response);

    /**
     * 获取滑动窗口空闲的窗口，这里做了一部分排队冗余
     * @return
     */
    protected int getFreeWindowSize(Window<Integer, ChannelWindowMessage, IMessage> slidingWindow) {
        return slidingWindow.getFreeSize();
    }

    protected long pullMsgToCache(LinkedBlockingQueue<IMessage> cacheMsg, MessageProvider messageProvider){
        //有缓存消息退出
        if (cacheMsg.size() > 0) {
            return cacheMsg.size();
        }
        //获取要发送的消息
        List<IMessage> sendMsgs = getSendMsgs(messageProvider);
        //获取到消息了占用滑动窗口并发送
        if (sendMsgs != null && sendMsgs.size() > 0) {
            cacheMsg.addAll(sendMsgs);
            return cacheMsg.size();
        }
        return 0;
    }

    protected List<IMessage> getSendMsgs(MessageProvider messageProvider){
        if (messageProvider != null) {
            return messageProvider.getTcpMessages(this);
        }
        return null;
    }

    protected IMessage getCacheMsg(LinkedBlockingQueue<IMessage> cacheMsg){
        return cacheMsg.poll();
    }

    /**
     * 滑动窗口释放slot
     *
     * @param message
     */
    protected void completeWindowMsg(IMessage message, Window<Integer, ChannelWindowMessage, IMessage> slidingWindow) {
        try {

            //一个channel对应一个线程，complete有阻塞方法，所以这里要使用异步
            WindowFuture<Integer, ChannelWindowMessage, IMessage> complete = slidingWindow.complete(message.getSequenceId(), message);

            //释放了一个slot，将队列中的执行
            sendWaitingMessage(slidingWindow);

            logger.debug("window slot complete {}", message.getSequenceId());

            ChannelWindowMessage windowMessage;
            if (complete != null) {
                windowMessage = complete.getRequest();
            } else {
                //查看延时缓存里面是否存在
                windowMessage = DelayResponseCache.getAndRemoveDelayMessage(getDelayCachedKey(message));
                logger.debug("cache response complete {}", message.getSequenceId());
            }

            if (windowMessage == null) {
                logger.error("can not find request message {}", message);
                handleResponseMatchFailed(getDelayCachedKey(message), message);
                return;
            }

            if (windowMessage.getMessage() == null) {
                logger.error("can not find key {}, message {}", message.getSequenceId(), message);
                return;
            }

            //统计窗口发送速率用
            Timer.Context timeContext = windowMessage.getTimeContext();
            if (timeContext != null) {
                timeContext.stop();
            }

            //超速重试
            if (needSendLater(windowMessage.getMessage(), message)) {
                logger.error("message over speed, response {}, request {}", message, windowMessage.getMessage());
                //网关异常时会发送大量超速错误(result=8),造成大量重发，浪费资源。这里先停止发送，过50毫秒再回恢复
                setChannelUnWritable(windowMessage.getCtx(), 50);
                //重发超速的,放到本地缓存重新发送
                DefaultEventGroupFactory.getInstance().getPullScheduleExecutor().schedule(() -> {
                    reSendMessage(windowMessage.getCtx(), windowMessage.getMessage(), windowMessage.getPromise());
                }, 100, TimeUnit.MILLISECONDS);
                return;
            }

            //区分发送失败和没有响应两种情况
            if (message instanceof SendFailMessage) {
                windowMessage.getMessage().handleMessageSendFailed(windowMessage.getMessage());
            } else {
                windowMessage.getMessage().handleMessageComplete(windowMessage.getMessage(), message);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("window message {} complete error InterruptedException", message.getSequenceId(), e);
        }
    }

    protected abstract void handleResponseMatchFailed(String requestKey, IMessage response);

    /**
     * 超速重新发送
     * @param message
     */
    public abstract void reSendMessage(ChannelHandlerContext ctx, IMessage message, ChannelPromise promise);

    protected void sendWaitingMessage(Window<Integer, ChannelWindowMessage, IMessage> slidingWindow) {
        //改写可写标识
        changeWritable(slidingWindow);
        WaitingMessage<Integer, ChannelWindowMessage> blockingMessage = slidingWindow.pollBlockingMessage();
        if (blockingMessage == null) {
            return;
        }
        ChannelWindowMessage channelWindowMessage = blockingMessage.getRequest();
        if (channelWindowMessage != null) {
            sendWindowMessage(channelWindowMessage.getCtx(), channelWindowMessage.getMessage(), channelWindowMessage.getPromise());
        }
    }

    protected abstract void changeWritable(Window<Integer, ChannelWindowMessage, IMessage> slidingWindow);

    public void expiredMessage(WindowFuture<Integer, ChannelWindowMessage, IMessage> future, Window<Integer, ChannelWindowMessage, IMessage> slidingWindow) {

        ChannelWindowMessage windowMessage = future.getRequest();
        if (future == null || windowMessage == null || windowMessage.getMessage() == null) {
            return;
        }
        Timer.Context timeContext = windowMessage.getTimeContext();
        if (timeContext != null) {
            timeContext.stop();
        }

        try {
            //超时回调
            handleMessageExpired(windowMessage);
        } catch (Exception e) {
            logger.error("expired error", e);
        }

        sendWaitingMessage(slidingWindow);
    }

    protected void setChannelUnWritable(final ChannelHandlerContext ctx, long milliTime){
        if(ctx.channel().isWritable()){
            setUserDefinedWritability(ctx, false);

            ctx.executor().schedule(new Runnable() {
                @Override
                public void run() {
                    setUserDefinedWritability(ctx, true);
                }
            }, milliTime, TimeUnit.MILLISECONDS);
        }
    }

    protected void setUserDefinedWritability(ChannelHandlerContext ctx, boolean writable) {
        ChannelOutboundBuffer cob = ctx.channel().unsafe().outboundBuffer();
        if (cob != null) {
            cob.setUserDefinedWritability(31, writable);
        }
    }

    private void handleMessageExpired(ChannelWindowMessage windowMessage) {

        IMessage message = windowMessage.getMessage();
        String delayCachedKey = getDelayCachedKey(message);
        //缓存要小于1000个，否则直接超时
        long cacheSize = DelayResponseCache.getCacheSize();
        if (cacheSize < 10000) {
            //把超时消息放到缓存里面等待响应
            DelayResponseCache.putDelayMessage(delayCachedKey, windowMessage);
            return;
        }

        SessionManager sessionManager = getSessionManager();
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        //触发接口中的超时处理
        if (customHandler != null) {
            //通知业务
            customHandler.responseMessageExpired(message.getSequenceId(), message);
        }
        message.handleMessageExpired(delayCachedKey, message);
    }

    /**
     * 获取超时缓存的唯一key
     * @param message
     * @return
     */
    protected abstract String getDelayCachedKey(IMessage message);

    public abstract void resetWindowSize(int windowSize);
}
