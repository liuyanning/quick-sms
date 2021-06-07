package com.drondea.sms.message;

/**
 * @version V3.0.0
 * @description: 消息体根类
 * @author: 刘彦宁
 * @date: 2020年06月05日13:46
 **/
public interface IHeader {
    /**
     * 是否是请求包
     *
     * @return
     */
    public boolean isRequest();

    /**
     * 是否是响应包
     *
     * @return
     */
    public boolean isResponse();
}
