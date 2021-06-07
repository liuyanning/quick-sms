package com.drondea.sms.common.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @version V3.0.0
 * @description: 优化System.currentTimeMillis()获取时间的性能问题
 * @author: 刘彦宁
 * @date: 2020年07月15日10:12
 **/
public class SystemClock {

    private final long period;
    private final AtomicLong now;

    private SystemClock(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(10);
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("System-Clock-%d").daemon(true).build());
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return now.get();
    }

    public static long now() {
        return instance().currentTimeMillis();
    }
}
