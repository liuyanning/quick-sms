/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.drondea.sms.handler.limiter;

import com.drondea.sms.type.DefaultEventGroupFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

@ChannelHandler.Sharable
public class ServerCounterLimitHandler extends AbstractCounterLimitHandler {
    /**
     * All queues per channel
     */
    private final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();

    private static final class PerChannel {
        long lastReadTimestamp;
    }

    /**
     * Create the global TrafficCounter.
     */
    void createGlobalTrafficCounter(ScheduledExecutorService executor) {
        TrafficCounter tc = new TrafficCounter(this,
                ObjectUtil.checkNotNull(executor, "executor"),
                "GlobalTC",
                checkInterval);

        setTrafficCounter(tc);
        tc.start();
    }

    /**
     * Create a new instance.
     *
     * @param readLimit     0 or a limit in bytes/s
     * @param checkInterval The delay between two computations of performances for
     *                      channels or 0 if no stats are to be computed.
     * @param maxTime       The maximum delay to wait in case of traffic excess.
     */
    public ServerCounterLimitHandler(String userName, long readLimit,
                                     long checkInterval, long maxTime) {
        super(readLimit, checkInterval, maxTime);
        ScheduledExecutorService executor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
        createGlobalTrafficCounter(executor);
    }

    public ServerCounterLimitHandler(String userName, long readLimit) {
        super(readLimit);
        ScheduledExecutorService executor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
        createGlobalTrafficCounter(executor);
    }

    /**
     * 服务器端关闭的时候要调用，释放counter
     */
    public final void release() {
        trafficCounter.stop();
    }

    public final void startCounter() {
        trafficCounter.start();
    }


    private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        // ensure creation is limited to one thread per channel
        Channel channel = ctx.channel();
        Integer key = channel.hashCode();
        PerChannel perChannel = channelQueues.get(key);
        if (perChannel == null) {
            perChannel = new PerChannel();
            perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            channelQueues.put(key, perChannel);
        }
        return perChannel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        getOrSetPerChannel(ctx);
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Integer key = channel.hashCode();
        channelQueues.remove(key);
        releaseReadSuspended(ctx);
        super.handlerRemoved(ctx);
    }

    @Override
    long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = channelQueues.get(key);
        if (perChannel != null) {
            if (wait > maxTime && now + wait - perChannel.lastReadTimestamp > maxTime) {
                wait = maxTime;
            }
        }
        return wait;
    }

    @Override
    void informReadOperation(final ChannelHandlerContext ctx, final long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = channelQueues.get(key);
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }

}
