package com.drondea.sms.message;

import com.drondea.sms.type.IMessageResponseHandler;

/**
 * @version V3.0.0
 * @description: 没有响应的消息体,用于发送失败的时候释放窗口空间
 * @author: 刘彦宁
 * @date: 2020年08月11日16:41
 **/
public class SendFailMessage implements IMessage{

    private int sequenceId;

    public SendFailMessage(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    @Override
    public int getSequenceId() {
        return sequenceId;
    }

    @Override
    public boolean isWindowResponseMessage() {
        return false;
    }

    @Override
    public boolean isWindowSendMessage() {
        return false;
    }

    @Override
    public int addRetryCount() {
        return 0;
    }

    @Override
    public void getRetryCount() {

    }

    @Override
    public boolean isRequest() {
        return false;
    }

    @Override
    public void handleMessageComplete(IMessage request, IMessage response) {

    }

    @Override
    public void handleMessageSendFailed(IMessage request) {

    }

    @Override
    public void handleMessageExpired(String cachedKey, IMessage request) {

    }

    @Override
    public void setMessageResponseHandler(IMessageResponseHandler messageResponseHandler) {

    }

    @Override
    public void setRequestMessage(IMessage iMessage) {

    }

    @Override
    public IMessage getRequestMessage() {
        return null;
    }

    @Override
    public void setSendTimeStamp(long timeStamp) {

    }

    @Override
    public long getSendTimeStamp() {
        return 0;
    }
}
