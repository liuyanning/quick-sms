package com.drondea.sms;

import com.drondea.sms.limiter.RateLimiter;
import com.drondea.sms.type.DefaultEventGroupFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @version V3.0.0
 * @description: 令牌桶测试
 * @author: 刘彦宁
 * @date: 2020年06月28日09:20
 **/
public class RateLimiterExample {

    public static void main(String[] args) throws InterruptedException {
        RateLimiter r = RateLimiter.create(100);
        int i = 0;
        while (true) {
            System.out.println("get " + i + " tokens: " + r.reserveTime(1) + "s");
            i++;
            if (i == 100) {
                break;
            }
        }
//        Thread.sleep(100);
        while (true) {
            System.out.println("get " + i + " tokens: " + r.reserveTime(1) + "s");
            i++;
            if (i == 200) {
                break;
            }
        }

//        int nTasks = 300;
//        int totalLimiter = 10;
//        CountDownLatch countDownLatch = new CountDownLatch(nTasks * totalLimiter);
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                }
//        long start = System.currentTimeMillis();
//        for(int j = 0; j < totalLimiter; j ++) {
//            ExecutorService executorService = new ThreadPoolExecutor(5, 10,
//                    10L, TimeUnit.SECONDS,
//                    new LinkedBlockingQueue<Runnable>());
//            RateLimiter rateLimiter = RateLimiter.create(2);
////            // qps设置为5，代表一秒钟只允许处理五个并发请求
//            for (int i = 0; i < nTasks; i++) {
//                final int job = i;
//
//                Future<?> submit = executorService.submit(() -> {
//                    double acquire = rateLimiter.acquire(1);
//                    System.out.println("等待了：" + acquire);
//                    System.out.println(Thread.currentThread().getName() + " gets job " + job + " done");
//                    countDownLatch.countDown();
//                });
//                submit.cancel(true);
//            }
//
//        }
////        executorService.shutdown();
//        countDownLatch.await();
//        long end = System.currentTimeMillis();
//        System.out.println(nTasks * totalLimiter + "jobs gets done by 5 threads concurrently in " + (end - start) + " milliseconds");


    }
}
