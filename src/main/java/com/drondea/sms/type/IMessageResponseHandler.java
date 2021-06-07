package com.drondea.sms.type;

import com.drondea.sms.message.IMessage;

/**
 * 消息回应完成处理
 *
 * @author liuyanning
 */
public interface IMessageResponseHandler {

    /**
     * 消息发送完成后收到响应的回调函数
     *
     * @param request
     * @param response
     */
    void messageComplete(IMessage request, IMessage response);


    /**
     * 滑动窗口超时
     *
     * @param request
     */
    void messageExpired(String cachedKey, IMessage request);


    void sendMessageFailed(IMessage request);
}
