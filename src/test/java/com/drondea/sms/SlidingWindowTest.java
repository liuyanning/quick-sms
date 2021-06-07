package com.drondea.sms;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.type.DefaultEventGroupFactory;
import com.drondea.sms.windowing.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @version V3.0.0
 * @description: 滑动窗口测试
 * @author: 刘彦宁
 * @date: 2020年06月24日09:42
 **/
public class SlidingWindowTest implements WindowListener<Integer, IMessage, IMessage> {

    ScheduledExecutorService monitorExecutor = DefaultEventGroupFactory.getInstance().getScheduleExecutor();
    Window<Integer, IMessage, IMessage> sendWindow = new Window<>(10, monitorExecutor,
            20 * 1000, this, "test.monitor");

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowTest test = new SlidingWindowTest();

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);

        for (int i = 0; i < 20; i++) {

            int finalI = i;
            fixedThreadPool.submit(() -> {
                try {
                    //一旦获取不到，后面的获取将被阻塞，所以时间设置多少要综合考虑
                    test.sendWindow.offer(finalI, new CmppSubmitRequestMessage(), 3 * 1000, 10 * 1000);
                    System.out.println("slidingWindow free size:" + test.sendWindow.getFreeSize());
                } catch (DuplicateKeyException e) {
                    System.out.println("主键重复");
                    e.printStackTrace();
                } catch (OfferTimeoutException e) {

                    System.out.println("获取slot超时:" + System.currentTimeMillis());
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException");
                    e.printStackTrace();
                }
            });

        }
        Thread.sleep(1000);
        System.out.println("当前正在等待的请求数量：" + test.sendWindow.getPendingOfferCount());
        int threadCount = ((ThreadPoolExecutor) fixedThreadPool).getActiveCount();
        System.out.println("getActiveCount:" + threadCount);
        Thread.sleep(3000);
        threadCount = ((ThreadPoolExecutor) fixedThreadPool).getActiveCount();
        System.out.println("getActiveCount:" + threadCount);
    }

    @Override
    public void expired(WindowFuture<Integer, IMessage, IMessage> future) {
        System.out.println("短信超时处理:" + future.getKey());
    }
}
