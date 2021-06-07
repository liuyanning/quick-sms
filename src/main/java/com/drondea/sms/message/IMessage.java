package com.drondea.sms.message;

import com.drondea.sms.type.IMessageResponseHandler;

/**
 * @version V3.0.0
 * @description: 基础包类型，所有包的父类
 * @author: 刘彦宁
 * @date: 2020年06月09日16:39
 **/
public interface IMessage {

    /**
     * 获取消息唯一id
     *
     * @return
     */
    int getSequenceId();

    /**
     * 是否是滑动窗口响应需要处理的消息
     *
     * @return
     */
    boolean isWindowResponseMessage();

    /**
     * 是否是滑动窗口需要处理的消息
     *
     * @return
     */
    boolean isWindowSendMessage();

    /**
     * 重新发送的次数
     */
    int addRetryCount();

    /**
     * 获取重试次数
     */
    void getRetryCount();

    boolean isRequest();

    /**
     * 消息异步完成以后回调处理
     */
    void handleMessageComplete(IMessage request, IMessage response);

    void handleMessageSendFailed(IMessage request);

    /**
     * 消息异步完成以后回调处理
     */
    void handleMessageExpired(String cachedKey, IMessage request);

    /**
     * 设置消息返回的回调
     *
     * @param messageResponseHandler
     */
    void setMessageResponseHandler(IMessageResponseHandler messageResponseHandler);

    /**
     * 响应包设置请求消息
     *
     * @param iMessage
     */
    void setRequestMessage(IMessage iMessage);

    /**
     * 响应包获取请求消息
     */
    IMessage getRequestMessage();


    /**
     * 设置消息发送出去时候的时间戳
     */
    void setSendTimeStamp(long timeStamp);

    long getSendTimeStamp();
}
