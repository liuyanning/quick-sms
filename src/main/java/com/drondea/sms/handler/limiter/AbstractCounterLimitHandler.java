/*
 * Copyright 2011 The Netty Project
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

import com.drondea.sms.message.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>AbstractTrafficShapingHandler allows to limit the global bandwidth
 * (see {@link ServerCounterLimitHandler}), as traffic shaping.
 * It allows you to implement an almost real time monitoring of the bandwidth using
 * the monitors from {@link TrafficCounter} that will call back every checkInterval
 * the method doAccounting of this handler.</p>
 *
 * <p>If you want for any particular reasons to stop the monitoring (accounting) or to change
 * the read/write limit or the check interval, several methods allow that for you:</p>
 * <ul>
 * <li><tt>configure</tt> allows you to change read or write limits, or the checkInterval</li>
 * <li><tt>getTrafficCounter</tt> allows you to have access to the TrafficCounter and so to stop
 * or start the monitoring, to change the checkInterval directly, or to have access to its values.</li>
 * </ul>
 */
public abstract class AbstractCounterLimitHandler extends ChannelDuplexHandler {
    private static final InternalLogger logger =
            InternalLoggerFactory.getInstance(AbstractCounterLimitHandler.class);
    /**
     * Default delay between two checks: 1s
     */
    public static final long DEFAULT_CHECK_INTERVAL = 1000;

    /**
     * Default max delay in case of traffic shaping
     * (during which no communication will occur).
     * Shall be less than TIMEOUT. Here half of "standard" 30s
     */
    public static final long DEFAULT_MAX_TIME = 15000;

    /**
     * Default minimal time to wait: 10ms
     */
    static final long MINIMAL_WAIT = 10;

    /**
     * Traffic Counter
     */
    protected TrafficCounter trafficCounter;

    /**
     * Limit in B/s to apply to read
     */
    private volatile long readLimit;

    /**
     * Max delay in wait
     */
    protected volatile long maxTime = DEFAULT_MAX_TIME; // default 15 s

    /**
     * Delay between two performance snapshots
     */
    protected volatile long checkInterval = DEFAULT_CHECK_INTERVAL; // default 1 s

    static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey
            .valueOf(AbstractCounterLimitHandler.class.getName() + ".READ_SUSPENDED");
    static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractCounterLimitHandler.class
            .getName() + ".REOPEN_TASK");


    /**
     * @param newTrafficCounter the TrafficCounter to set
     */
    void setTrafficCounter(TrafficCounter newTrafficCounter) {
        trafficCounter = newTrafficCounter;
    }

    /**
     * @param readLimit     0 or a limit in bytes/s
     * @param checkInterval The delay between two computations of performances for
     *                      channels or 0 if no stats are to be computed.
     * @param maxTime       The maximum delay to wait in case of traffic excess.
     *                      Must be positive.
     */
    protected AbstractCounterLimitHandler(long readLimit, long checkInterval, long maxTime) {
        if (maxTime <= 0) {
            throw new IllegalArgumentException("maxTime must be positive");
        }

        this.readLimit = readLimit;
        this.checkInterval = checkInterval;
        this.maxTime = maxTime;
    }


    /**
     * Constructor using default Check Interval value of {@value #DEFAULT_CHECK_INTERVAL} ms and
     * default max time as delay allowed value of {@value #DEFAULT_MAX_TIME} ms.
     *
     * @param readLimit 0 or a limit in bytes/s
     */
    protected AbstractCounterLimitHandler(long readLimit) {
        this(readLimit, DEFAULT_CHECK_INTERVAL, DEFAULT_MAX_TIME);
    }

    /**
     * Change the underlying limitations and check interval.
     * <p>Note the change will be taken as best effort, meaning
     * that all already scheduled traffics will not be
     * changed, but only applied to new traffics.</p>
     * <p>So the expected usage of this method is to be used not too often,
     * accordingly to the traffic shaping configuration.</p>
     *
     * @param newReadLimit     The new read limit (in bytes)
     * @param newCheckInterval The new check interval (in milliseconds)
     */
    public void configure(long newReadLimit,
                          long newCheckInterval) {
        configureRead(newReadLimit);
        configure(newCheckInterval);
    }

    /**
     * Change the underlying limitations.
     * <p>Note the change will be taken as best effort, meaning
     * that all already scheduled traffics will not be
     * changed, but only applied to new traffics.</p>
     * <p>So the expected usage of this method is to be used not too often,
     * accordingly to the traffic shaping configuration.</p>
     *
     * @param newReadLimit The new read limit (in bytes)
     */
    public void configureRead(long newReadLimit) {
        readLimit = newReadLimit;
        if (trafficCounter != null) {
            trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    /**
     * Change the check interval.
     *
     * @param newCheckInterval The new check interval (in milliseconds)
     */
    public void configure(long newCheckInterval) {
        checkInterval = newCheckInterval;
        if (trafficCounter != null) {
            trafficCounter.configure(checkInterval);
        }
    }

    /**
     * @return the readLimit
     */
    public long getReadLimit() {
        return readLimit;
    }

    /**
     * <p>Note the change will be taken as best effort, meaning
     * that all already scheduled traffics will not be
     * changed, but only applied to new traffics.</p>
     * <p>So the expected usage of this method is to be used not too often,
     * accordingly to the traffic shaping configuration.</p>
     *
     * @param readLimit the readLimit to set
     */
    public void setReadLimit(long readLimit) {
        this.readLimit = readLimit;
        if (trafficCounter != null) {
            trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    /**
     * @return the checkInterval
     */
    public long getCheckInterval() {
        return checkInterval;
    }

    /**
     * @param checkInterval the interval in ms between each step check to set, default value being 1000 ms.
     */
    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
        if (trafficCounter != null) {
            trafficCounter.configure(checkInterval);
        }
    }

    /**
     * <p>Note the change will be taken as best effort, meaning
     * that all already scheduled traffics will not be
     * changed, but only applied to new traffics.</p>
     * <p>So the expected usage of this method is to be used not too often,
     * accordingly to the traffic shaping configuration.</p>
     *
     * @param maxTime Max delay in wait, shall be less than TIME OUT in related protocol.
     *                Must be positive.
     */
    public void setMaxTimeWait(long maxTime) {
        if (maxTime <= 0) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.maxTime = maxTime;
    }

    /**
     * @return the max delay in wait to prevent TIME OUT
     */
    public long getMaxTimeWait() {
        return maxTime;
    }


    /**
     * Called each time the accounting is computed from the TrafficCounters.
     * This method could be used for instance to implement almost real time accounting.
     *
     * @param counter the TrafficCounter that computes its performance
     */
    protected void doAccounting(TrafficCounter counter) {
        // NOOP by default
    }

    /**
     * Class to implement setReadable at fix time
     */
    static final class ReopenReadTimerTask implements Runnable {
        final ChannelHandlerContext ctx;

        ReopenReadTimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            Channel channel = ctx.channel();
            ChannelConfig config = channel.config();
            if (!config.isAutoRead() && isHandlerActive(ctx)) {
                // If AutoRead is False and Active is True, user make a direct setAutoRead(false)
                // Then Just reset the status
                if (logger.isDebugEnabled()) {
                    logger.debug("Not unsuspend: " + config.isAutoRead() + ':' +
                            isHandlerActive(ctx));
                }
                channel.attr(READ_SUSPENDED).set(false);
            } else {
                // Anything else allows the handler to reset the AutoRead
                if (logger.isDebugEnabled()) {
                    if (config.isAutoRead() && !isHandlerActive(ctx)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Unsuspend: " + config.isAutoRead() + ':' +
                                    isHandlerActive(ctx));
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Normal unsuspend: " + config.isAutoRead() + ':'
                                    + isHandlerActive(ctx));
                        }
                    }
                }
                channel.attr(READ_SUSPENDED).set(false);
                config.setAutoRead(true);
                channel.read();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unsuspend final status => " + config.isAutoRead() + ':'
                        + isHandlerActive(ctx));
            }
        }
    }


    /**
     * Release the Read suspension
     */
    void releaseReadSuspended(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channel.attr(READ_SUSPENDED).set(false);
        channel.config().setAutoRead(true);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        //计算消息的流量
        long size = calculateSize(msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0) {
            // compute the number of ms to wait before reopening the channel
            long wait = trafficCounter.readTimeToWait(size, readLimit, maxTime, now);
            wait = checkWaitReadTime(ctx, wait, now);
            // At least 10ms seems a minimal time in order to try to limit the traffic
            if (wait >= MINIMAL_WAIT) {
                // Only AutoRead AND HandlerActive True means Context Active
                Channel channel = ctx.channel();
                ChannelConfig config = channel.config();
                if (logger.isDebugEnabled()) {
                    logger.debug("Read suspend: " + wait + ':' + config.isAutoRead() + ':'
                            + isHandlerActive(ctx));
                }
                if (config.isAutoRead() && isHandlerActive(ctx)) {
                    config.setAutoRead(false);
                    channel.attr(READ_SUSPENDED).set(true);
                    // Create a Runnable to reactive the read if needed. If one was create before it will just be
                    // reused to limit object creation
                    Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
                    Runnable reopenTask = attr.get();
                    if (reopenTask == null) {
                        reopenTask = new ReopenReadTimerTask(ctx);
                        attr.set(reopenTask);
                    }
                    ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Suspend final status => " + config.isAutoRead() + ':'
                                + isHandlerActive(ctx) + " will reopened at: " + wait);
                    }
                }
            }
        }
        informReadOperation(ctx, now);
        ctx.fireChannelRead(msg);
    }

    /**
     * Method overridden in GTSH to take into account specific timer for the channel.
     *
     * @param wait the wait delay computed in ms
     * @param now  the relative now time in ms
     * @return the wait to use according to the context
     */
    long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
        // no change by default
        return wait;
    }

    /**
     * Method overridden in GTSH to take into account specific timer for the channel.
     *
     * @param now the relative now time in ms
     */
    void informReadOperation(final ChannelHandlerContext ctx, final long now) {
        // default noop
    }

    protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
        Boolean suspended = ctx.channel().attr(READ_SUSPENDED).get();
        return suspended == null || Boolean.FALSE.equals(suspended);
    }

    @Override
    public void read(ChannelHandlerContext ctx) {
        if (isHandlerActive(ctx)) {
            // For Global Traffic (and Read when using EventLoop in pipeline) : check if READ_SUSPENDED is False
            ctx.read();
        }
    }


    /**
     * @return the current TrafficCounter (if
     * channel is still connected)
     */
    public TrafficCounter trafficCounter() {
        return trafficCounter;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(290)
                .append("TrafficShaping with ")
                .append(" Read Limit: ").append(readLimit)
                .append(" CheckInterval: ").append(checkInterval)
                .append(" and Counter: ");
        if (trafficCounter != null) {
            builder.append(trafficCounter);
        } else {
            builder.append("none");
        }
        return builder.toString();
    }

    /**
     * Calculate the size of the given {@link Object}.
     * <p>
     * This implementation supports {@link ByteBuf} and {@link ByteBufHolder}. Sub-classes may override this.
     *
     * @param msg the msg for which the size should be calculated.
     * @return size the size of the msg or {@code -1} if unknown.
     */
    protected long calculateSize(Object msg) {
        //如果是请求的话算一个size
        if (msg instanceof IMessage) {
            return ((IMessage) msg).isRequest() ? 1 : -1;
        }
        return -1;
    }
}
