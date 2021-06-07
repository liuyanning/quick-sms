package com.drondea.sms.type;

/**
 * @version V3.0.0
 * @description: 自定义异常
 * @author: 刘彦宁
 * @date: 2020年06月05日11:52
 **/
public class ChannelException extends Exception {

    public ChannelException(String msg) {
        super(msg);
    }

    public ChannelException(String msg, Throwable t) {
        super(msg, t);
    }
}
