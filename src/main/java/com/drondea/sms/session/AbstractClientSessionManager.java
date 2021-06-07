package com.drondea.sms.session;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.limiter.RateLimiter;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.type.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * @version V3.0.0
 * @description: 默认的连接管理器, 负责创建和维护连接
 * @author: 刘彦宁
 * @date: 2020年06月05日14:27
 **/
public abstract class AbstractClientSessionManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientSessionManager.class);
    /**
     * 连接器，负责创建连接
     */
    protected IConnector connector;
    /**
     * 客户端配置
     */
    protected ClientSocketConfig clientSocketConfig;

    /**
     * 客户端关联的所有channel
     */
    private ChannelGroup channels;

    /**
     * 客户端关联的session对象
     */
    private List<ChannelSession> sessions;


    private ScheduledFuture<?> scheduledFuture;

    /**
     * 总流速限速
     */
    private RateLimiter rateLimiter;
    /**
     * 令牌桶限流策略线程池
     */
    private ExecutorService qpsLimitExecutor;

    /**
     * 用户定制处理器
     */
    private ICustomHandler customInterface;

    private CounterRateLimiter counterRateLimiter;

    private MessageProvider messageProvider;

    public List<ChannelSession> getSessions() {
        return sessions;
    }

    /**
     * 监控的度量器
     */
    private Timer clientWindowTimer;

    public AbstractClientSessionManager(ClientSocketConfig clientSocketConfig, ICustomHandler customInterface) {

        if (clientSocketConfig == null) {
            return;
        }
        this.clientSocketConfig = clientSocketConfig;
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        //读多写少用这个
        this.sessions = new CopyOnWriteArrayList<>();
        this.connector = getConnector();
        if (clientSocketConfig.getQpsLimit() > 0 && getMessageProvider() == null) {
            this.rateLimiter = RateLimiter.create(clientSocketConfig.getQpsLimit());
//            DefaultEventGroupFactory instance = DefaultEventGroupFactory.getInstance();
            //一个通道绑定一个线程池
//            qpsLimitExecutor = new ThreadPoolExecutor(1,
//                    1,
//                    0L, TimeUnit.SECONDS,
//                    new LinkedBlockingQueue<Runnable>(),
//                    new BasicThreadFactory.Builder().namingPattern("qpsLimitPool-%d").
//                            uncaughtExceptionHandler(instance.getExceptionHandler()).build(),
//                    instance.getRejectedExcutionHandler());
        }
        this.customInterface = customInterface;
        String id = clientSocketConfig.getId();
        if (GlobalConstants.METRICS_ON) {
            MetricRegistry registry = Metrics.getInstance().getRegistry();
            this.clientWindowTimer = registry.timer("clientWindowTimer:" + id);
        }
    }

    /**
     * 返回连接器
     *
     * @return 客户端连接器
     */
    protected abstract IConnector getConnector();

    @Override
    public void doOpen() {
        int channelSize = clientSocketConfig.getChannelSize();
        int openSessionSize = getSessionSize();

        //关闭未登陆成功的session
        sessions.stream().filter(session -> session.getState() != ChannelSession.STATE_LOGIN_SUCCESS).
                forEach(session -> session.close());

        logger.debug("{} 检测连接，需要连接 {},当前连接数 {}", clientSocketConfig.getId(), channelSize, openSessionSize);
        //循环打开所有的连接
        for (int i = 0; i < channelSize - openSessionSize; i++) {
            logger.debug("创建连接");
            connector.bind(clientSocketConfig);
        }

    }

    @Override
    public void doClose() {
        logger.debug("关闭session");
        //停止schedule检测任务
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
        channels.close();
        if (this.clientWindowTimer != null) {
            String id = clientSocketConfig.getId();
            Metrics.remove("clientWindowTimer:" + id);
        }
    }

    @Override
    public void doCheckSessions() {
        this.scheduledFuture = DefaultEventGroupFactory.getInstance().getScheduleExecutor().scheduleWithFixedDelay(() -> {
            logger.debug("check connection");
            try {
                doOpen();
            } catch (Exception e) {
                logger.error("do open fail", e);
            }

        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public SocketConfig getSocketConfig() {
        return this.clientSocketConfig;
    }

    @Override
    public void addSession(ChannelSession session) {
        this.channels.add(session.getChannel());
        this.sessions.add(session);
    }

    @Override
    public void removeSession(ChannelSession session) {
        this.channels.remove(session);
        this.sessions.remove(session);
    }

    @Override
    public int getSessionSize() {
        return this.sessions.size();
    }


    @Override
    public ICustomHandler getCustomHandler() {
        return this.customInterface;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public ExecutorService getQpsLimitExecutor() {
        return qpsLimitExecutor;
    }

    public void setQpsLimitExecutor(ExecutorService qpsLimitExecutor) {
        this.qpsLimitExecutor = qpsLimitExecutor;
    }

    @Override
    public Timer getWindowTimer() {
        return clientWindowTimer;
    }

    public int getSessionIndex(ChannelSession session) {
        return this.sessions.indexOf(session);
    }

    public CounterRateLimiter getCounterRateLimiter() {
        return counterRateLimiter;
    }

    /**
     * 重新调整发送速度
     * @param permitsPerSecond
     */
    public void resetSpeed(int permitsPerSecond){
        if (rateLimiter != null) {
            rateLimiter.setRate(permitsPerSecond);
        }
        if (counterRateLimiter != null) {
            counterRateLimiter.setPermitsPerSecond(permitsPerSecond);
        }
        clientSocketConfig.setQpsLimit(permitsPerSecond);
    }

    public void resetWindowSize(int windowSize){
        sessions.forEach(session -> {
            session.resetWindowSize(windowSize);
        });
        clientSocketConfig.setWindowSize(windowSize);
    }

    @Override
    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    @Override
    public void setMessageProvider(MessageProvider messageProvider) {
        if (clientSocketConfig.getQpsLimit() > 0) {
            this.counterRateLimiter = new CounterRateLimiter(clientSocketConfig.getQpsLimit());
        }
        this.messageProvider = messageProvider;
    }
}
