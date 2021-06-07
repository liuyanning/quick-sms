package com.drondea.sms;

/**
 * @version V3.0.0
 * @description:
 * @author: 刘彦宁
 * @date: 2020年08月27日10:05
 **/
public class StringThreadTest {

    public static class TestService {
        public void show(String content) {
            synchronized (content) {
                while(true) {
                    System.out.println(Thread.currentThread().getName());
                    try {
                        Thread.sleep(5000);
                        System.out.println(content);
                        break;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class MyTaskThread implements Runnable{
        TestService ts = new TestService();
        @Override
        public void run() {
            // TODO Auto-generated method stub
            ts.show("aa".intern());
        }
    }

    public static class MyTaskThreadB implements Runnable{
        TestService ts = new TestService();
        @Override
        public void run() {
            // TODO Auto-generated method stub
            ts.show("bb".intern());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTaskThread mtt = new MyTaskThread();
        MyTaskThreadB mt2 = new MyTaskThreadB();
        Thread t = new Thread(mtt);
        Thread t1 = new Thread(mtt);
        Thread t2 = new Thread(mt2);
        t.start();
        t1.start();
        t2.start();
    }
}
