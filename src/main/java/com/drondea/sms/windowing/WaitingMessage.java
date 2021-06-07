package com.drondea.sms.windowing;

/**
 * @version V3.0.0
 * @description: 等待处理的窗口消息
 * @author: 刘彦宁
 * @date: 2020年07月03日09:20
 **/
public class WaitingMessage<K, R> {
    private final K key;
    private final R request;

    public WaitingMessage(K key, R request) {
        this.key = key;
        this.request = request;
    }

    public K getKey() {
        return key;
    }

    public R getRequest() {
        return request;
    }
}
