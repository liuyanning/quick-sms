package com.drondea.sms.session;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.handler.limiter.ServerCounterLimitHandler;
import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.type.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @version V3.0.0
 * @description: 服务器端的session管理器
 * @author: 刘彦宁
 * @date: 2020年06月10日14:16
 **/
public abstract class AbstractServerSessionManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServerSessionManager.class);

    /**
     * 连接器，负责创建连接
     */
    private IConnector connector;
    /**
     * 客户端配置
     */
    private ServerSocketConfig serverSocketConfig;

    /**
     * 服务器关联的所有channel
     */
    private ChannelGroup channels;

    //todo 没用的话去掉
    /**
     * 服务器关联的session对象
     */
    private List<ChannelSession> sessions;

    private ConcurrentHashMap<String, ServerChannelGroup> userChannelGroupMap;

    /**
     * sessionid生成器
     */
    private final AtomicLong sessionIdSequence;

    /**
     * 用户定制处理器
     */
    private ICustomHandler customInterface;

    private MessageProvider messageProvider;

    public AbstractServerSessionManager(ServerSocketConfig serverSocketConfig, ICustomHandler customInterface) {
        this.serverSocketConfig = serverSocketConfig;
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        //一定要注意synchronizedList遍历没有加锁，不是线程安全的
        this.sessions = new CopyOnWriteArrayList<>();
        this.userChannelGroupMap = new ConcurrentHashMap<>();
//        this.userSessionMap = new ConcurrentHashMap<>();
//        this.counterLimitHandlerMap = new ConcurrentHashMap<>();
//        this.userQPSLimiterMap = new ConcurrentHashMap<>();
        this.connector = getConnector();
        //sessionId生成器
        this.sessionIdSequence = new AtomicLong(0);
        this.customInterface = customInterface;

        //metric监控
        if (GlobalConstants.METRICS_ON) {
            MetricRegistry registry = Metrics.getInstance().getRegistry();
            String id = serverSocketConfig.getId();
            //监控服务器端channels个数
            registry.register("ServerChannelCount:" + id, (Gauge<Integer>) () -> channels.size());
        }
    }

    @Override
    public void doOpen() {
        connector.bind(serverSocketConfig);
    }

    /**
     * 返回连接器
     *
     * @return 客户端连接器
     */
    public abstract IConnector getConnector();

    /**
     * 根据用户名称获取用户信息
     *
     * @param userName
     * @return
     */
    public abstract UserChannelConfig getUserChannelConfig(String userName);

    public AtomicLong getSessionIdSequence() {
        return this.sessionIdSequence;
    }

    public long nextSessionId() {
        return this.sessionIdSequence.incrementAndGet();
    }

    @Override
    public void doClose() {
        if (channels != null && channels.size() > 0) {
            channels.close();
        }
    }

    @Override
    public void doCheckSessions() {
        logger.debug("server side need not check sessions");
    }

    @Override
    public SocketConfig getSocketConfig() {
        return serverSocketConfig;
    }

    @Override
    public void addSession(ChannelSession session) {
        this.channels.add(session.getChannel());
        this.sessions.add(session);
    }

    /**
     * 添加用户session，
     *
     * @param channelConfig
     * @param session
     * @return
     */
    public boolean addUserSession(UserChannelConfig channelConfig, AbstractServerSession session) {

        String userName = channelConfig.getUserName();
        int limit = channelConfig.getChannelLimit();
        session.setUserName(userName);

        String protocolType = session.getSessionType();
        String userLock = "quick_" + userName;
        //针对用户加锁
        synchronized (userLock.intern()) {
            //最大连接数判断
            ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
            if (limit != 0 && serverChannelGroup != null && serverChannelGroup.getChannelSize() >= limit) {
                return false;
            }
            //第一次连接添加对象
            if (serverChannelGroup == null) {
                serverChannelGroup = new ServerChannelGroup(userName);
                userChannelGroupMap.put(userName, serverChannelGroup);
                serverChannelGroup.setProtocolType(protocolType);
                //metric监控
                if (GlobalConstants.METRICS_ON) {
                    MetricRegistry registry = Metrics.getInstance().getRegistry();
                    //监控服务器端channels个数
                    registry.register(protocolType + ":UserChannelCount:" + userName, (Gauge<Integer>) () -> getUserSessionSize(userName));
                }
            }
            serverChannelGroup.addSession(session);
            //设置限速
            setRateLimiter(userName, channelConfig.getQpsLimit());
            IChannelSessionCounters counters = session.getCounters();
            if (counters != null) {
                counters.setMetricsCounter(userName + ":server:" + session.getChannel().id());
            }

            return true;
        }
    }

    /**
     * 设置限速
     *
     * @param userName
     * @param qpsLimit
     */
    private void setRateLimiter(String userName, int qpsLimit) {
        if (qpsLimit <= 0) {
            return;
        }
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        //限速方式2
        if (2 == CmppConstants.DEFAULT_SERVER_RATELIMITER_TYPE) {
            ServerCounterLimitHandler rateLimiter = (ServerCounterLimitHandler) serverChannelGroup.getCounterLimitHandler();
            if (rateLimiter == null) {
                rateLimiter = new ServerCounterLimitHandler(userName, qpsLimit);
                rateLimiter.startCounter();
            } else {
                long readLimit = rateLimiter.getReadLimit();
                if (readLimit != qpsLimit) {
                    //新连接过来可以改变速度
                    rateLimiter.configureRead(qpsLimit);
                }
            }
            serverChannelGroup.setCounterLimitHandler(rateLimiter);
            return;
        }
        //限速方式1
        CounterRateLimiter rateLimiter = serverChannelGroup.getCounterRateLimiter();
        if (rateLimiter == null) {
            rateLimiter = new CounterRateLimiter(qpsLimit);
        } else {
            //新连接过来可以改变速度
            rateLimiter.setPermitsPerSecond(qpsLimit);
        }
        serverChannelGroup.setCounterRateLimiter(rateLimiter);
    }

    private void releaseResource(ChannelSession session) {
        String userName = ((AbstractServerSession) session).getUserName();
        if (userName == null) {
            return;
        }
        int userSessionSize = getUserSessionSize(userName);
        if (userSessionSize > 0) {
            return;
        }
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup == null) {
            return;
        }
        ServerCounterLimitHandler rateLimiter = (ServerCounterLimitHandler) serverChannelGroup.getCounterLimitHandler();
        if (rateLimiter == null) {
            return;
        }
        //用户没有连接的时候把计数器取消
        rateLimiter.release();
        String protocolType = serverChannelGroup.getProtocolType();
        MetricRegistry registry = Metrics.getInstance().getRegistry();
        //监控服务器端channels个数
        registry.remove(protocolType + ":UserChannelCount:" + userName);
    }

    @Override
    public void removeSession(ChannelSession session) {
        if (session == null) {
            return;
        }
        if (!(session instanceof AbstractServerSession)) {
            return;
        }
        AbstractServerSession serverSession = (AbstractServerSession) session;
        String userName = serverSession.getUserName();
        String userLock = "quick_" + userName;
        //针对用户加锁
        synchronized (userLock.intern()) {
            //移除计数器
            IChannelSessionCounters counters = session.getCounters();
            if (counters != null) {
                counters.reset();
            }
            this.removeUserSession(session);
            this.channels.remove(session);
            this.sessions.remove(session);
            releaseResource(session);
        }
    }

    public void removeUserSession(ChannelSession session) {
        if (session == null || userChannelGroupMap == null) {
            return;
        }
        String userName = null;
        if (session instanceof AbstractServerSession) {
            AbstractServerSession serverSession = (AbstractServerSession) session;
            userName = serverSession.getUserName();
        }
        if (userName == null) {
            return;
        }
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup != null) {
            serverChannelGroup.removeSession(session);
        }
    }

    public int getUserSessionSize(String userName) {
        if (userChannelGroupMap == null || userName == null) {
            return 0;
        }
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup != null) {
            return serverChannelGroup.getChannelSize();
        }
        return 0;
    }

    public List<ChannelSession> getUserSessions(String userName) {
        if (userChannelGroupMap == null || userName == null) {
            return null;
        }
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup != null) {
            return serverChannelGroup.getSessions();
        }
        return null;
    }

    @Override
    public int getSessionSize() {
        return this.sessions.size();
    }

    @Override
    public ICustomHandler getCustomHandler() {
        return this.customInterface;
    }

    public CounterRateLimiter getUserQPSLimiter(String userName) {
        return userChannelGroupMap.get(userName).getCounterRateLimiter();
    }

    public AbstractCounterLimitHandler getCounterLimitHandler(String userName) {
        return userChannelGroupMap.get(userName).getCounterLimitHandler();
    }

    public void closeUserSession(String userName) {
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup != null) {
            serverChannelGroup.getSessions().forEach(channelSession -> {
                channelSession.close();
            });
        }
    }

    /**
     * 修改用户的回执滑动窗口数
     * @param userName
     * @param windowSize
     */
    public void resetUserWindowSize(String userName, int windowSize) {
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup != null) {
            serverChannelGroup.getSessions().forEach(channelSession -> {
                channelSession.resetWindowSize(windowSize);
            });
        }
    }

    /**
     * 修改用户的提交速度
     * @param userName
     * @param qpsLimit
     */
    public void resetUserSubmitSpeed(String userName, long qpsLimit) {
        ServerChannelGroup serverChannelGroup = userChannelGroupMap.get(userName);
        if (serverChannelGroup == null) {
            return;
        }
        ServerCounterLimitHandler rateLimiter = (ServerCounterLimitHandler) serverChannelGroup.getCounterLimitHandler();
        if (rateLimiter != null) {
            long readLimit = rateLimiter.getReadLimit();
            if (readLimit != qpsLimit) {
                //新连接过来可以改变速度
                rateLimiter.configureRead(qpsLimit);
                logger.info("user {} change speed to {}", userName, qpsLimit);
            }
        }
        //是否是第二种限速方式
        CounterRateLimiter rateLimiter2 = serverChannelGroup.getCounterRateLimiter();
        if (rateLimiter2 != null) {
            //新连接过来可以改变速度
            rateLimiter2.setPermitsPerSecond(qpsLimit);
        }
    }


    @Override
    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    @Override
    public Timer getWindowTimer() {
        //服务器端暂时不设置timer
        return null;
    }

    @Override
    public void setMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    /**
     * 获取所有登陆的用户
     * @return
     */
    public Set<String> getAllLoginUser() {
        return userChannelGroupMap.keySet();
    }
}
