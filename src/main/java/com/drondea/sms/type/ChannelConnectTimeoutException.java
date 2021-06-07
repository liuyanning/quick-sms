package com.drondea.sms.type;

/**
 * @version V3.0.0
 * @description: 连接超时
 * @author: 刘彦宁
 * @date: 2020年06月05日11:54
 **/
public class ChannelConnectTimeoutException extends ChannelException {

    public ChannelConnectTimeoutException(String msg) {
        super(msg);
    }

    public ChannelConnectTimeoutException(String msg, Throwable t) {
        super(msg, t);
    }
}
