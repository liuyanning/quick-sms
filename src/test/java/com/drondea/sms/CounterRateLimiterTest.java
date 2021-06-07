package com.drondea.sms;

import com.drondea.sms.limiter.CounterRateLimiter;
import com.drondea.sms.type.DefaultEventGroupFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V3.0.0
 * @description: 计数器限速测试
 * @author: 刘彦宁
 * @date: 2020年07月04日13:02
 **/
public class CounterRateLimiterTest {

    public static void main(String[] args) throws InterruptedException {
        CounterRateLimiter counterRateLimiter = new CounterRateLimiter(2000);
        ScheduledExecutorService monitorExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
        AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0; i < 1600; i++) {
            monitorExecutor.submit(() -> {
                boolean tryAcquire = counterRateLimiter.tryAcquire();
                if (tryAcquire) {
                    System.out.println("获取到权限：" + atomicInteger.incrementAndGet());
                    ;
                }
            });
        }
//        Thread.sleep(2000);
//        System.out.println(System.currentTimeMillis());
//        for (int i = 0; i < 10; i++) {
//            new Thread(()->{
//                for (int j = 0; j < 301; j++) {
//                    try {
//                        Thread.sleep(2, 450000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    boolean tryAcquire = counterRateLimiter.tryAcquire();
//                    if (!tryAcquire) {
//                        System.out.println("未获取到权限：" + atomicInteger.incrementAndGet());;
//                    }
//                    if (j == 300) {
//                        System.out.println(System.currentTimeMillis());
//                    }
//                }
//            }).start();
//        }

        Thread.sleep(2000);
        System.out.println("第二次获取权限");
        for (int i = 0; i < 2200; i++) {
            monitorExecutor.submit(() -> {
                boolean tryAcquire = counterRateLimiter.tryAcquire();
                if (tryAcquire) {
                    System.out.println("获取到权限2：" + atomicInteger.incrementAndGet());
                    ;
                }
            });
        }
        Thread.sleep(2000);
        System.out.println("第3次获取权限");
        for (int i = 0; i < 2200; i++) {
            monitorExecutor.submit(() -> {
                boolean tryAcquire = counterRateLimiter.tryAcquire();
                if (tryAcquire) {
                    System.out.println("获取到权限3：" + atomicInteger.incrementAndGet());
                    ;
                }
            });
        }

    }
}
