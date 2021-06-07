package com.drondea.sms.handler;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.session.AbstractServerSession;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.type.Metrics;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @version V3.0.0
 * @description: 服务器端做速率统计
 * @author: 刘彦宁
 * @date: 2020年07月07日18:28
 **/
@ChannelHandler.Sharable
public class ServerMetricsMeterHandler extends ChannelDuplexHandler {

    /**
     * 根据userName映射的Meter
     */
    private ConcurrentHashMap<String, Meter> userMeterMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AbstractServerSession channelSession = (AbstractServerSession) CommonUtil.getChannelSession(ctx.channel());
        String userName = channelSession.getUserName();
        Meter meter = getMeter(userName);
        if (meter != null) {
            meter.mark();
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        AbstractServerSession channelSession = (AbstractServerSession) CommonUtil.getChannelSession(ctx.channel());
        String userName = channelSession.getUserName();
        addMeter(userName);
        super.handlerAdded(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AbstractServerSession channelSession = (AbstractServerSession) CommonUtil.getChannelSession(ctx.channel());
        AbstractServerSessionManager serverSessionManager = (AbstractServerSessionManager) channelSession.getSessionManager();

        String userName = channelSession.getUserName();
        int userSessionSize = serverSessionManager.getUserSessionSize(userName);
        if (userSessionSize == 0) {
            removeMeter(userName);
        }
        super.handlerRemoved(ctx);
    }

    private synchronized void addMeter(String userName) {
        Meter meter = userMeterMap.get(userName);
        if (meter == null) {
            MetricRegistry registry = Metrics.getInstance().getRegistry();
            meter = registry.meter("serverMeter:" + userName);
            userMeterMap.put(userName, meter);
        }
    }

    private void removeMeter(String userName) {
        Meter meter = userMeterMap.get(userName);
        if (meter != null) {
            Metrics.remove("serverMeter:" + userName);
            userMeterMap.remove(userName);
        }
    }

    private Meter getMeter(String userName) {
        return userMeterMap.get(userName);
    }
}
