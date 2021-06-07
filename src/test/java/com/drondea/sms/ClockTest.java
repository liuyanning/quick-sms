package com.drondea.sms;

import com.drondea.sms.common.util.SystemClock;

/**
 * @version V3.0.0
 * @description: 测试时钟效率
 * @author: 刘彦宁
 * @date: 2020年07月15日17:42
 **/
public class ClockTest {

    public static void main(String[] args) {

        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 100000000; i++) {
            SystemClock.now();
        }
        System.out.println(System.currentTimeMillis());

        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 100000000; i++) {
            System.currentTimeMillis();
        }
        System.out.println(System.currentTimeMillis());
    }
}
