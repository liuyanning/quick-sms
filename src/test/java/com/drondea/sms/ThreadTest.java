package com.drondea.sms;

import com.drondea.sms.type.DefaultEventGroupFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * @version V3.0.0
 * @description: 线程池测试
 * @author: 刘彦宁
 * @date: 2020年07月01日09:48
 **/
public class ThreadTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println(System.currentTimeMillis());
        try {
            Thread.sleep(0, 20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis());
        ScheduledThreadPoolExecutor QPS_SCHEDULE_EXECUTOR = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("qpsSchedulePool-%d").build());
        QPS_SCHEDULE_EXECUTOR.schedule(() -> {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.MILLISECONDS);

        QPS_SCHEDULE_EXECUTOR.schedule(() -> {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.MILLISECONDS);

        QPS_SCHEDULE_EXECUTOR.schedule(() -> {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.MILLISECONDS);
        Thread.sleep(30 * 1000);
        System.out.println(QPS_SCHEDULE_EXECUTOR.getQueue().size());

//        ThreadPoolExecutor tpe = new ThreadPoolExecutor(
//                0, 2, 30000, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>(1));
//        System.out.println("init pool size= " + tpe.getPoolSize() + ", queue size=" + tpe.getQueue().size());
//
//        tpe.execute(new Task("1st", 10000));
//        Thread.sleep(1000);
//        print(tpe, "1st");
//
//        tpe.execute(new Task("2nd", 0));
//        Thread.sleep(1000);
//        print(tpe, "2nd");
//
//        tpe.execute(new Task("3d", 2000));
//        Thread.sleep(1000);
//        print(tpe, "3d");
//
//        while (tpe.getPoolSize()>0) {
//            Thread.sleep(100);
//        }
//        System.out.println("pool size= " + tpe.getPoolSize() + ", queue size=" + tpe.getQueue().size());
//        tpe.shutdown();
//        Thread.sleep(1000000);
    }

    private static void print(ThreadPoolExecutor tpe, String name) {
        System.out.println("After " + name + " execute -  pool size= " + tpe.getPoolSize() + ", queue=" + tpe.getQueue());
    }

    private static class Task implements Runnable {

        private final String name;
        private final long time;

        Task(String name, long time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public void run() {
            System.out.println("Run " + Thread.currentThread().getName() + "-" + name);
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Finish " + Thread.currentThread().getName() + "-" + name);
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
