package com.drondea.sms.limiter;


import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @version V3.0.0
 * @description: 计数器限流
 * @author: 刘彦宁
 * @date: 2020年07月04日12:09
 **/
public class CounterRateLimiter {

    private AtomicLong counter = new AtomicLong();
    private long timestamp = System.nanoTime();
    //    private Stopwatch stopwatch = Stopwatch.createStarted();
    private long permitsPerSecond;
    /**
     * 1秒包含10^6微妙
     */
    private long timeLimit = SECONDS.toNanos(1L);

    public CounterRateLimiter(long permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
//        timestamp = stopwatch.elapsed(MICROSECONDS);
    }

    /**
     * 获取1个限速额度
     *
     * @return
     */
    public boolean tryAcquire() {
//        long now = stopwatch.elapsed(MICROSECONDS);
        long now = System.nanoTime();
        if (now - timestamp < timeLimit) {
            if (counter.incrementAndGet() <= permitsPerSecond) {
                return true;
            }
            return false;
        }

        synchronized (counter) {
            if (now - timestamp > timeLimit) {
                counter.set(1);
                timestamp = now;
                return true;
            }

            if (counter.incrementAndGet() <= permitsPerSecond) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void setPermitsPerSecond(long permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public long getPermitsPerSecond() {
        return permitsPerSecond;
    }
}
