package com.drondea.sms.type;

/**
 * @version V3.0.0
 * @description: 连接异常类
 * @author: 刘彦宁
 * @date: 2020年06月05日11:53
 **/
public class ChannelConnectException extends ChannelException {

    public ChannelConnectException(String msg) {
        super(msg);
    }

    public ChannelConnectException(String msg, Throwable t) {
        super(msg, t);
    }
}
