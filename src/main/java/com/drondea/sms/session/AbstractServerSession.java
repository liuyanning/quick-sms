package com.drondea.sms.session;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.common.CommonSequenceNumber;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.message.SendFailMessage;
import com.drondea.sms.type.*;
import com.drondea.sms.windowing.ChannelWindowMessage;
import com.drondea.sms.windowing.Window;
import com.drondea.sms.windowing.WindowFuture;
import com.drondea.sms.windowing.WindowListener;
import io.netty.channel.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @version V3.0.0
 * @description: 服务器端session处理器
 * @author: 刘彦宁
 * @date: 2020年06月10日16:20
 **/
public abstract class AbstractServerSession extends ChannelSession implements WindowListener<Integer, ChannelWindowMessage, IMessage> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServerSession.class);

    /**
     * session 当前状态
     */
    private final AtomicInteger state;

    private final ServerSocketConfig configuration;
    private final Channel channel;

    private byte interfaceVersion;
    private ScheduledExecutorService monitorExecutor;
    private IChannelSessionCounters counters;
    private SessionManager sessionManager;
    //todo 一个channel的sequenceNumber不能重复么,暂定一个channel一个sequenceNumber
    private final SequenceNumber sequenceNumber;

    private long sessionId;
    private String userName;

    private UserChannelConfig userChannelConfig;

    private String sessionType;

    /**
     * 滑动窗口对象，每个channel持有一个
     */
    private Window<Integer, ChannelWindowMessage, IMessage> slidingWindow;

    private AtomicInteger qpsWaitingSize;

    /**
     * session的可读标识volatile保证多线程的可见性,0为可写，1为不可写
     */
    private volatile int unwritable;

    private static final AtomicIntegerFieldUpdater<AbstractServerSession> UNWRITABLE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(AbstractServerSession.class, "unwritable");

    @Override
    public int getWaitSize() {
        return slidingWindow.getBlockingMessageSize();
    }

    private int windowLimitSize = 50;

    /**
     * session事件处理器
     */
    private ISessionEventHandler sessionEventHandler;

    private final LinkedBlockingQueue<IMessage> cacheMsg = new LinkedBlockingQueue();
    private final ChannelHandlerContext ctx;
    private MessageProvider messageProvider;
    private boolean isPullMode;

    /**
     * 创建session管理器
     */
    public AbstractServerSession(ChannelHandlerContext ctx, SessionManager sessionManager) {
        this.state = new AtomicInteger(STATE_INITIAL);
        this.configuration = (ServerSocketConfig) sessionManager.getSocketConfig();
        this.sessionManager = sessionManager;
        this.ctx = ctx;
        this.channel = ctx.channel();
        this.sequenceNumber = new CommonSequenceNumber();

        if (configuration.isCountersEnabled() || GlobalConstants.METRICS_ON) {
            this.counters = sessionManager.createSessionCounters();
        }
        this.sessionId = ((AbstractServerSessionManager) sessionManager).nextSessionId();
    }

    @Override
    public void fireChannelActive() {
        logger.debug("server channel active");
        setState(STATE_ACTIVE);
    }

    /**
     * 处理连接请求
     *
     * @param message
     */
    public abstract void dealConnectRequest(IMessage message);

    @Override
    public ChannelFuture sendMessage(IMessage message) {
        //不可写状态直接返回,并且重试
        if (!this.channel.isWritable()) {
            logger.debug("当前channel不可写,重试");
            if (!this.channel.isActive()) {
                logger.info("当前channel不可写,已关闭");
                return null;
            }
            ScheduledExecutorService scheduleExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
            scheduleExecutor.schedule(() -> {
                sendMessage(message);
            }, 1000, TimeUnit.MICROSECONDS);
            return null;
        }

        ChannelFuture channelFuture = this.channel.write(message);
        //滑动窗口不需要flush
        if (message.isWindowSendMessage() && this.getSlidingWindow() != null) {
            return channelFuture;
        }
//        //限速消息也不要flush
//        if (this.userChannelConfig != null && this.userChannelConfig.getQpsLimit() > 0 && message.isWindowSendMessage()) {
//            return channelFuture;
//        }

        this.channel.flush();
        return channelFuture;
    }

    public void doAfterLogin(UserChannelConfig channelConfig) {
        this.userChannelConfig = channelConfig;
        int windowSize = channelConfig.getWindowSize();
        if (windowSize > 0 && channelConfig.getWindowMonitorInterval() > 0) {
            monitorExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
            String monitorThreadName = channelConfig.getId() + ".Monitor." + channel.id();
            this.slidingWindow = new Window<>(windowSize, monitorExecutor,
                    channelConfig.getWindowMonitorInterval(), this, monitorThreadName);
        }
        if (userChannelConfig.getQpsLimit() > 0) {
            this.qpsWaitingSize = new AtomicInteger();
        }

        if (sessionManager.getMessageProvider() != null && this.slidingWindow != null) {
            this.isPullMode = true;
            this.messageProvider = sessionManager.getMessageProvider();
            //延时1秒，开始拉取消息
            delayPullWindowMsg(1000);
        }
    }

    @Override
    public void fireMsgReceived(IMessage message) {
        //如果通道已经处于active状态，处理登录消息
        if (STATE_ACTIVE == getState()) {
            dealConnectRequest(message);
        }
        if (getCounters() != null) {
            countTXMessage(message);
        }

        if (slidingWindow == null || !message.isWindowResponseMessage()) {
            return;
        }

        //滑动窗口消息处理
        completeWindowMsg(message, slidingWindow);

    }

    @Override
    public void fireChannelClosed() {
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler != null) {
            customHandler.channelClosed(this);
        }
        this.channel.close();
    }

    @Override
    public boolean fireWrite(ChannelHandlerContext ctx, IMessage msg, ChannelPromise promise) {
        if (getCounters() != null) {
            countRXMessage(msg);
        }
        //不是窗口和限速消息类型
        if (!msg.isWindowSendMessage()) {
            ctx.write(msg, promise);
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
    protected void notifyChannelLoginSuccess(Channel channel) {
        //触发登录成功事件
        channel.pipeline().fireUserEventTriggered(STATE_LOGIN_SUCCESS);
    }

    @Override
    protected void pullAndSendWindowMsgs() {
        int delay = 10;
        //查看是否可写，不可写延时
        if (!this.channel.isActive()) {
            return;
        }

        if (!this.channel.isWritable()) {
            delay = 100;
            //定时拉取
            delayPullWindowMsg(delay);
            return;
        }

        //查看滑动窗口空闲个数n
        int freeSize = getFreeWindowSize(this.slidingWindow);
        if (freeSize == 0) {
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

            //未达到限速条件写入一个片段
            IMessage cacheMsg = getCacheMsg(this.cacheMsg);
            if (cacheMsg != null) {
                sendWindowMessage(this.ctx , cacheMsg, this.ctx.newPromise());
            }
        }

        delayPullWindowMsg(delay);
    }


    @Override
    public void fireWritabilityChanged(boolean writable) {
        if (writable && qpsWaitingSize != null && qpsWaitingSize.get() > windowLimitSize / 2) {
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

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ServerSocketConfig getConfiguration() {
        return this.configuration;
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
    public int getState() {
        return this.state.get();
    }

    protected void setState(int state) {
        this.state.set(state);
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
    public SequenceNumber getSequenceNumber() {
        return this.sequenceNumber;
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
        int bolocingMessageSize = slidingWindow.getBlockingMessageSize();
        if (bolocingMessageSize < windowLimitSize/2) {
            logger.debug("滑动窗口触发可写状态改变：等待数{},{}", bolocingMessageSize, true);
            fireWritabilityChanged(true);
        }

    }

    @Override
    public void sendWindowMessage(ChannelHandlerContext ctx, IMessage message, ChannelPromise promise) {

        try {

            ChannelWindowMessage windowMessage = new ChannelWindowMessage(ctx, message, promise);
            //会占用线程等待超时，所以时间设置多少要综合考虑
            WindowFuture offer = slidingWindow.offer(message.getSequenceId(), windowMessage, 20000,
                    userChannelConfig.getRequestExpiryTimeout());
            if (!this.isPullMode) {
                int bolocingMessageSize = slidingWindow.getBlockingMessageSize();
                if (bolocingMessageSize > windowLimitSize) {
                    logger.debug("滑动窗口触发可写状态改变：等待数{},{}", bolocingMessageSize, false);
                    fireWritabilityChanged(false);
                }
            }

            if (offer != null) {
                //打印当前等待获取slot的key
                logger.debug("sliding window pending offer key {}", offer.getKey());

                //设置真正的发送时间
                message.setSendTimeStamp(System.currentTimeMillis());

                //用户线程执行writeAndFlush，会在netty队列中执行,这里必须是ctx，否则可能死循环
                ChannelFuture channelFuture = ctx.writeAndFlush(message, promise);
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        logger.error("window message send failure, {}, {}", message, channelFuture);
                        SendFailMessage sendFailMessage = new SendFailMessage(message.getSequenceId());
                        DefaultEventGroupFactory.getInstance().getScheduleExecutor().submit(() -> {
                            completeWindowMsg(sendFailMessage, slidingWindow);
                        });
                    }
                });
            }

            //拉取模式的计数方法
            if (configuration.isCountersEnabled() && this.isPullMode) {
                //计数
                countTXMessage(message);
            }

        } catch (Exception e) {
            logger.error("key already exists in the window", message.getSequenceId(), e);
        }
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected void setInterfaceVersion(byte value) {
        this.interfaceVersion = value;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void expired(WindowFuture<Integer, ChannelWindowMessage, IMessage> future) {
        expiredMessage(future, slidingWindow);
    }

    /**
     * 校验用户的ip地址是否合法
     *
     * @param userChannelConfig
     * @param channel
     * @return
     */
    protected boolean validIpAddress(UserChannelConfig userChannelConfig, Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        String clientIp = remoteAddress.getAddress().getHostAddress();
        String whiteIp = userChannelConfig.getValidIp();
        return StringUtils.isEmpty(whiteIp)
                || Arrays.asList(whiteIp.split(GlobalConstants.MUTL_MOBILE_SPLIT)).stream()
                .anyMatch(item -> item.equals(clientIp));
    }


    public void failedLogin(IMessage msg, long status) {
        ICustomHandler customHandler = sessionManager.getCustomHandler();
        if (customHandler == null) {
            return;
        }
        customHandler.failedLogin(this, msg, status);
    }

    public UserChannelConfig getUserChannelConfig() {
        return userChannelConfig;
    }

    public void setUserChannelConfig(UserChannelConfig userChannelConfig) {
        this.userChannelConfig = userChannelConfig;
    }

    public ISessionEventHandler getSessionEventHandler() {
        return sessionEventHandler;
    }

    @Override
    public void setSessionEventHandler(ISessionEventHandler sessionEventHandler) {
        this.sessionEventHandler = sessionEventHandler;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    @Override
    protected String getDelayCachedKey(IMessage message) {
        return userChannelConfig.getId() + ":" + this.channel.id() + ":" + message.getSequenceId();
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
        int windowSizeOld = userChannelConfig.getWindowSize();
        if (windowSizeOld != windowSize) {
            this.slidingWindow.setMaxSize(windowSize);
            userChannelConfig.setWindowSize(windowSize);
        }
    }

    @Override
    protected void handleResponseMatchFailed(String requestKey, IMessage response) {
        if (messageProvider != null) {
            messageProvider.responseMessageMatchFailed(requestKey, response);
        }
    }
}
