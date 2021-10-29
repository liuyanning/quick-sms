package com.drondea.sms.session;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.common.CommonSequenceNumber;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.limiter.RateLimiter;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.SendFailMessage;
import com.drondea.sms.type.*;
import com.drondea.sms.windowing.ChannelWindowMessage;
import com.drondea.sms.windowing.Window;
import com.drondea.sms.windowing.WindowFuture;
import com.drondea.sms.windowing.WindowListener;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author liuyannning
 */
public abstract class AbstractClientSession extends ChannelSession implements WindowListener<Integer, ChannelWindowMessage, IMessage> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientSession.class);

    /**
     * session 当前状态
     */
    private final AtomicInteger state;
    /**
     * 登录时间
     */
    private final AtomicLong loginTime;
    private final ClientSocketConfig configuration;
    private final Channel channel;
    //todo 一个channel的sequenceNumber不能重复么,暂定一个channel一个sequenceNumber
    private final SequenceNumber sequenceNumber;
    //滑动窗口
    private Window<Integer, ChannelWindowMessage, IMessage> slidingWindow;
    private byte interfaceVersion;
    private final LinkedBlockingQueue<IMessage> cacheMsg = new LinkedBlockingQueue();

    private ScheduledExecutorService monitorExecutor;
    private IChannelSessionCounters counters;
    private SessionManager sessionManager;

    private AtomicInteger qpsWaitingSize;
    //端口号
    private int localPort;


    /**
     * session事件处理器
     */
    private ISessionEventHandler sessionEventHandler;
    private int qpsLimitSize = 50;

    private CounterRateLimiter counterLimiter;
    private final ChannelHandlerContext ctx;
    private MessageProvider messageProvider;
    private boolean isPullMode;

    /**
     * 创建session管理器
     */
    public AbstractClientSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        this.state = new AtomicInteger(STATE_INITIAL);
        this.configuration = (ClientSocketConfig) sessionManager.getSocketConfig();
        this.ctx = ctx;
        this.channel = ctx.channel();
        this.loginTime = new AtomicLong(0);
        this.sequenceNumber = new CommonSequenceNumber();

        this.sessionManager = sessionManager;
        InetSocketAddress socket = (InetSocketAddress) this.channel.localAddress();
        this.localPort = socket.getPort();

        if (configuration.isCountersEnabled()) {
            this.counters = sessionManager.createSessionCounters();
        }

    }

    @Override
    public void fireChannelActive() {
        logger.debug("channel active");
        //通道激活发送登录消息,客户端逻辑
        sendLoginMsg();
    }

    @Override
    public void fireMsgReceived(IMessage message) {
        //收到了连接成功的消息，改变session状态，并增加后续心跳检测，业务处理等handler
        //正处于登录前ing状态
        if (getState() == STATE_LOGINING) {
            //处理登录响应信息
            dealConnectResponseMessage(message);
        }

        if (getCounters() != null) {
            countRXMessage(message);
        }

        if (getState() != STATE_LOGIN_SUCCESS) {
            return;
        }

        if (slidingWindow == null || !message.isWindowResponseMessage()) {
            return;
        }

        //滑动窗口消息处理
        completeWindowMsg(message, slidingWindow);
    }


    @Override
    public boolean fireWrite(ChannelHandlerContext ctx, IMessage msg, ChannelPromise promise) {
        if (getCounters() != null) {
            countTXMessage(msg);
        }

        //不是窗口和限速消息类型
        if (!msg.isWindowSendMessage() || this.isPullMode) {
            ctx.write(msg, promise);
            return true;
        }

        AbstractClientSessionManager clientSessionManager = (AbstractClientSessionManager) getSessionManager();
        RateLimiter rateLimiter = clientSessionManager.getRateLimiter();
        //流量控制消息处理
        if (rateLimiter != null && qpsWaitingSize != null) {
            sendQPSLimitMessage(ctx, rateLimiter, msg, promise, this.slidingWindow != null);
            return true;
        }
        //不需要流量控制,只需要滑动窗口
        if (this.slidingWindow != null) {
            this.sendWindowMessage(ctx, msg, promise);
            return true;
        }
        //不需要流量控制和滑动窗口
        ctx.write(msg, promise);
        return true;
    }

    @Override
    public void fireWritabilityChanged(boolean writable) {
        //netty触发可写状态时候，如果限速队列等待的数量过多也不能写
        if (writable && qpsWaitingSize != null && qpsWaitingSize.get() > qpsLimitSize / 2) {
            return;
        }
        if (writable) {
            setWritable();
        } else {
            setUnwritable();
        }
    }

    private void setWritable() {
        setUserDefinedWritability(true);
    }

    private void setUnwritable() {
        setUserDefinedWritability(false);
    }

    private void setUserDefinedWritability(boolean writable) {
        ChannelOutboundBuffer cob = this.channel.unsafe().outboundBuffer();
        if (cob != null) {
            cob.setUserDefinedWritability(31, writable);
        }
    }

    private void fireChannelWritabilityChanged(boolean writable){
        ISessionEventHandler sessionEventHandler = getSessionEventHandler();
        //触发事件
        if (sessionEventHandler != null) {
            sessionEventHandler.sessionWritablityChanged(writable);
        }
    }

    private void sendQPSLimitMessage(ChannelHandlerContext ctx,RateLimiter rateLimiter, IMessage msg, ChannelPromise promise, boolean isWindowMsg) {
        ScheduledExecutorService scheduleExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
        //在发送消息前进行限速
        long reserveTime = rateLimiter.reserveTime(1);
        if (reserveTime > 0) {
            int qpsWait = qpsWaitingSize.incrementAndGet();
            if (qpsWait > qpsLimitSize) {
                logger.debug("限速触发可写状态改变：等待数{},{}", qpsWait, false);
                fireWritabilityChanged(false);
            }
//                System.out.println("需要等待：" + (reserveTime * 1.0)/1000000 );
            //延时指定时间执行
            scheduleExecutor.schedule(() -> {
                int waitSize = qpsWaitingSize.decrementAndGet();
                //非滑动窗口要根据限速排队来改变可写状态,滑动窗口也要兼顾滑动窗口的排队量
                if (waitSize <= qpsLimitSize / 2) {
                    if (isWindowMsg) {
                        int bolocingMessageSize = slidingWindow.getBlockingMessageSize();
                        if (bolocingMessageSize < qpsLimitSize / 2) {
                            logger.debug("限速触发可写状态改变：等待数{},{},窗口排队数{}", bolocingMessageSize, true, bolocingMessageSize);
                            fireWritabilityChanged(true);
                        }
                    } else {
                        logger.debug("限速触发可写状态改变：等待数{},是否可写{}", waitSize, true);
                        fireWritabilityChanged(true);
                    }
                }
                sendQPSMessage(ctx, msg, promise, isWindowMsg);
//                    System.out.println("等待：" + (reserveTime * 1.0)/1000000 + "任务数：" + waitSize);
            }, reserveTime, TimeUnit.MICROSECONDS);
            logger.trace("拿到本次token, 需要等待 {} 微妙", reserveTime);
            return;
        }

        //发送消息,区分窗口消息和普通消息
        sendQPSMessage(ctx, msg, promise, isWindowMsg);
    }

    /**
     * 发送消息封装了一下区分滑动窗口和非滑动窗口
     *
     * @param ctx
     * @param msg
     * @param promise
     * @param isWindowMsg
     */
    private void sendQPSMessage(ChannelHandlerContext ctx, IMessage msg, ChannelPromise promise, boolean isWindowMsg) {
        if (isWindowMsg) {
            this.sendWindowMessage(ctx, msg, promise);
        } else {
            ctx.writeAndFlush(msg, promise);
        }
    }

    @Override
    public ChannelFuture sendMessage(IMessage message) {
        ChannelFuture channelFuture = this.channel.write(message);
        //滑动窗口不需要flush
        if (message.isWindowSendMessage() && this.getSlidingWindow() != null) {
            return channelFuture;
        }
        ClientSocketConfig configuration = getConfiguration();
        //限速消息也不要flush
        int qpsLimit = configuration.getQpsLimit();
        if (message.isWindowSendMessage() && qpsLimit > 0) {
            return channelFuture;
        }
        this.channel.flush();
        return channelFuture;
    }

    /**
     * 记录发送数据，做统计使用
     */
    protected abstract void sendLoginMsg();

    /**
     * 处理登录响应
     *
     * @param message
     * @return 是否登录成功
     */
    public abstract boolean dealConnectResponseMessage(IMessage message);


    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public SequenceNumber getSequenceNumber() {
        return this.sequenceNumber;
    }


    @Override
    public ClientSocketConfig getConfiguration() {
        return this.configuration;
    }

    protected void setState(int state) {
        this.state.set(state);
        if (state == STATE_LOGIN_SUCCESS) {
            this.loginTime.set(System.currentTimeMillis());
        }
    }

    public long getLoginTime() {
        return this.loginTime.get();
    }

    @Override
    public void close() {
        //关闭滑动窗口和滑动窗口的定时监听
        if (this.slidingWindow != null) {
            this.slidingWindow.destroy();
        }

        if (this.counters != null) {
            this.counters.reset();
        }
        this.channel.close();
    }

    @Override
    public void fireChannelClosed() {
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.channelClosed(this);
        }
        if (GlobalConstants.METRICS_ON) {
            String id = getConfiguration().getId();
            Metrics.remove("clientChannelQPSSize:" + id + ":" + this.channel.id());
            Metrics.remove("clientChannelWindowSize:" + id + ":" + this.channel.id());
        }
        close();
    }

    public boolean customLoginValid(IMessage message) {
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            return customHandler.customLoginValidate(message, null, getChannel());
        }
        return true;
    }

    @Override
    public int getState() {
        return this.state.get();
    }

    @Override
    public String getStateName() {
        int s = this.state.get();
        if (s >= 0 || s < STATES.length) {
            return STATES[s];
        } else {
            return "UNKNOWN (" + s + ")";
        }
    }

    @Override
    public IChannelSessionCounters getCounters() {
        return counters;
    }

    @Override
    public byte getInterfaceVersion() {
        return 0;
    }

    @Override
    public boolean areOptionalParametersSupported() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isBinding() {
        return false;
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean isUnbinding() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }


    protected void setInterfaceVersion(byte value) {
        this.interfaceVersion = value;
    }

    @Override
    protected void notifyChannelLoginSuccess(Channel channel) {
        //触发登录成功事件
        channel.pipeline().fireUserEventTriggered(STATE_LOGIN_SUCCESS);
    }

    @Override
    protected void pullAndSendWindowMsgs() {
        int delay = 10;
        //通道关闭直接return
        if (!this.channel.isActive()) {
            return;
        }
        //查看是否可写，不可写延时
        if (!this.channel.isWritable()) {
            delay = 50;
            //定时拉取
            delayPullWindowMsg(delay);
            return;
        }

        //查看滑动窗口空闲个数,没有滑动窗口延时
        int freeSize = getFreeWindowSize(this.slidingWindow);
        if (freeSize <= 0) {
            delay = 30;
            delayPullWindowMsg(delay);
            return;
        }
        //循环写入滑动窗口
        for (int i = 0; i < freeSize; i++) {
            //拉取数据到本地缓存
            long cacheSize = pullMsgToCache(this.cacheMsg, this.messageProvider);
            //没有数据直接退出并延时重试
            if (cacheSize == 0) {
                break;
            }
            //查看是否达到限速条件，达到限速条件退出，延时获取
            if (this.counterLimiter != null && !this.counterLimiter.tryAcquire()) {
                delay = 50;
                break;
            }

            //判断是否可写
            if (!this.channel.isWritable()) {
                delay = 50;
                break;
            }
            //未达到限速条件写入一个片段
            IMessage cacheMsg = getCacheMsg(this.cacheMsg);
            if (cacheMsg != null) {
                sendWindowMessage(this.ctx ,cacheMsg, ctx.newPromise());
            }
        }

        delayPullWindowMsg(delay);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public Window<Integer, ChannelWindowMessage, IMessage> getSlidingWindow() {
        return this.slidingWindow;
    }

    @Override
    protected void changeWritable(Window<Integer, ChannelWindowMessage, IMessage> slidingWindow) {
        if (this.isPullMode) {
            return;
        }

        int blockingMessageSize = slidingWindow.getBlockingMessageSize();
        //限速+滑动窗口，滑动窗口队列要少，限速不能有排队才变为可写状态,只是利用滑动窗口发送，改变可写状态的条件
        if (blockingMessageSize < qpsLimitSize / 2) {
            if (qpsWaitingSize != null) {
                int waitSize = qpsWaitingSize.get();
                if (waitSize < qpsLimitSize / 2) {
                    logger.debug("滑动窗口1触发可写状态改变true：等待数{},{}", blockingMessageSize, true);
                    fireWritabilityChanged(true);
                }
            } else {
                logger.debug("滑动窗口2触发可写状态改变true：等待数{},{}", blockingMessageSize, true);
                fireWritabilityChanged(true);
            }
        }
    }

    @Override
    public void sendWindowMessage(ChannelHandlerContext ctx, IMessage message, ChannelPromise promise) {

        try {
            ChannelWindowMessage windowMessage = new ChannelWindowMessage(ctx, message, promise);
            //会占用线程等待超时，所以时间设置多少要综合考虑
            WindowFuture offer = slidingWindow.offer(message.getSequenceId(), windowMessage, 20000,
                    configuration.getRequestExpiryTimeout());

            if (!this.isPullMode) {
                int blockingMessageSize = slidingWindow.getBlockingMessageSize();
                if (blockingMessageSize > qpsLimitSize) {
                    logger.debug("滑动窗口触发可写状态改变：等待数{},{}", blockingMessageSize, false);
                    fireWritabilityChanged(false);
                }
            }

            if (offer != null) {
                //打印当前等待获取slot的key
                logger.debug("sliding window pending offer key {}", offer.getKey());

                SessionManager sessionManager = getSessionManager();
                //统计滑动窗口的请求速率，请求时间-响应时间
                Timer clientWindowTimer = sessionManager.getWindowTimer();
                if (clientWindowTimer != null) {
                    Timer.Context timeContext = clientWindowTimer.time();
                    windowMessage.setTimeContext(timeContext);
                }
                //设置真正的发送时间
                message.setSendTimeStamp(System.currentTimeMillis());
                //用户线程执行writeAndFlush，会在netty队列中执行,这里必须是ctx，否则可能死循环
                ChannelFuture channelFuture = ctx.writeAndFlush(message, promise);
                logger.debug("write channel: {}", message);
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        logger.error("window message send failure, {}, {}", message, channelFuture);
                        SendFailMessage sendFailMessage = new SendFailMessage(message.getSequenceId());
                        DefaultEventGroupFactory.getInstance().getCompleteExecutor().submit(() -> {
                            completeWindowMsg(sendFailMessage, slidingWindow);
                        });
                    }
                });

                //拉取模式的计数方法
                if (configuration.isCountersEnabled() && this.isPullMode) {
                    //计数
                    countTXMessage(message);
                }
            }

        } catch (Exception e) {
            logger.error("key already exists in the window", message.getSequenceId(), e);
        }
    }

    public void doAfterLogin() {

        int windowSize = configuration.getWindowSize();
        if (windowSize > 0 && configuration.getWindowMonitorInterval() > 0) {
            monitorExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
            String monitorThreadName = configuration.getId() + ".Monitor." + channel.id();
            this.slidingWindow = new Window<>(windowSize, monitorExecutor,
                    configuration.getWindowMonitorInterval(), this, monitorThreadName);
        }
        if (configuration.getQpsLimit() > 0) {
            this.qpsWaitingSize = new AtomicInteger();
        }

        AbstractClientSessionManager clientSessionManager = (AbstractClientSessionManager) getSessionManager();
        this.counterLimiter = clientSessionManager.getCounterRateLimiter();

        //中间件主动拉取消息模式
        if (clientSessionManager.getMessageProvider() != null && this.slidingWindow != null) {
            this.isPullMode = true;
            this.messageProvider = clientSessionManager.getMessageProvider();
            //延时1秒，开始拉取消息
            delayPullWindowMsg(1000);
        }

        if (GlobalConstants.METRICS_ON) {
            String id = getConfiguration().getId();
            MetricRegistry registry = Metrics.getInstance().getRegistry();
            if (qpsWaitingSize != null) {
                //超速等待队列大小
                registry.register("clientChannelQPSSize:" + id + ":" + this.channel.id(),
                        (Gauge<Integer>) () -> qpsWaitingSize.get()
                );
            }

            if (slidingWindow != null) {
                //窗口等待队列大小
                registry.register("clientChannelWindowSize:" + id + ":" + this.channel.id(),
                        (Gauge<Integer>) () -> slidingWindow.getBlockingMessageSize()
                );
            }
        }
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public int getWaitSize() {
        return qpsWaitingSize == null ? 00 : qpsWaitingSize.get();
    }

    @Override
    protected String getDelayCachedKey(IMessage message) {
        return getConfiguration().getId() + ":" + this.channel.id() + ":" + message.getSequenceId();
    }

    /**
     * 滑动窗口超时处理，这里要尽快处理，不能延时太长
     *
     * @param future
     */
    @Override
    public void expired(WindowFuture<Integer, ChannelWindowMessage, IMessage> future) {
        expiredMessage(future, slidingWindow);
    }

    public ISessionEventHandler getSessionEventHandler() {
        return sessionEventHandler;
    }

    @Override
    public void setSessionEventHandler(ISessionEventHandler sessionEventHandler) {
        this.sessionEventHandler = sessionEventHandler;
    }

    @Override
    public void reSendMessage(ChannelHandlerContext ctx, IMessage message, ChannelPromise promise) {
        if (messageProvider != null) {
            this.cacheMsg.offer(message);
        } else {
            sendWindowMessage(ctx, message, promise);
        }
    }

    @Override
    public void resetWindowSize(int windowSize) {
        this.slidingWindow.setMaxSize(windowSize);
    }

    @Override
    protected void handleResponseMatchFailed(String requestKey, IMessage response) {
        if (messageProvider != null) {
            messageProvider.responseMessageMatchFailed(requestKey, response);
        }
    }

    /**
     * 获取本地连接的端口号
     * @return
     */
    public int getLocalPort() {
        return this.localPort;
    }
}
