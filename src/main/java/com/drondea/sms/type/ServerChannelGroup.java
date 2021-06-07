package com.drondea.sms.type;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.handler.limiter.AbstractCounterLimitHandler;
import com.drondea.sms.limiter.CounterRateLimiter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version V3.0.0
 * @description: 服务器端用户分组资源，每个客户对应的资源
 * @author: 刘彦宁
 * @date: 2020年07月16日13:57
 **/
public class ServerChannelGroup {

    private String userName;

    private String protocolType;

    /**
     * 客户的所有session
     */
    private List<ChannelSession> sessions;

    /**
     * 客户对应的限速器
     */
    private CounterRateLimiter counterRateLimiter;

    /**
     * 另外一种限速器实现
     */
    private AbstractCounterLimitHandler counterLimitHandler;

    public ServerChannelGroup(String userName) {
        this.userName = userName;
        sessions = new CopyOnWriteArrayList<>();
    }

    public void addSession(ChannelSession channelSession) {
        sessions.add(channelSession);
    }

    public boolean removeSession(ChannelSession channelSession) {
        return sessions.remove(channelSession);
    }


    public int getChannelSize() {
        if (sessions == null) {
            return 0;
        }
        return sessions.size();
    }

    public List<ChannelSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<ChannelSession> sessions) {
        this.sessions = sessions;
    }

    public CounterRateLimiter getCounterRateLimiter() {
        return counterRateLimiter;
    }

    public void setCounterRateLimiter(CounterRateLimiter counterRateLimiter) {
        this.counterRateLimiter = counterRateLimiter;
    }

    public AbstractCounterLimitHandler getCounterLimitHandler() {
        return counterLimitHandler;
    }

    public void setCounterLimitHandler(AbstractCounterLimitHandler counterLimitHandler) {
        this.counterLimitHandler = counterLimitHandler;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }
}
