package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.IMessageResponseHandler;

/**
 * @version V3.0.0
 * @description: cmpp包父类
 * @author: 刘彦宁
 * @date: 2020年06月08日14:23
 **/
public abstract class AbstractCmppMessage implements IMessage, Cloneable {

    private IPackageType packageType;
    private long timestamp;
    private long sendTimestamp;
    /**
     * 消息的生命周期，单位秒, 0表示永不过期
     */
    private long lifeTime = 0;
    private CmppHeader header;

    /**
     * 发送重试次数
     */
    private int retryCount = 0;

    private IMessage requestMessage;

    private IMessageResponseHandler messageResponseHandler;

    public AbstractCmppMessage(IPackageType packageType) {
        setPackageType(packageType);
        CmppHeader header = new CmppHeader(packageType);
        header.setCommandId(packageType.getCommandId());
        setHeader(header);
    }

    public AbstractCmppMessage(IPackageType packageType, CmppHeader header) {
        setPackageType(packageType);
        if (header == null) {
            header = new CmppHeader(packageType.getCommandId());

            header.setSequenceId(GlobalConstants.sequenceNumber.next());
        } else {
            header.setCommandId(packageType.getCommandId());
        }
        setHeader(header);
    }

    public IPackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(IPackageType packageType) {
        this.packageType = packageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public CmppHeader getHeader() {
        return header;
    }

    public void setHeader(CmppHeader header) {
        this.header = header;
    }

    public int getHeaderLength() {
        return getHeader().getHeadLength();
    }

    /**
     * 获取消息的body长度3.0版本的
     *
     * @return
     */
    public abstract int getBodyLength30();

    /**
     * 获取消息的body长度2.0版本的
     *
     * @return
     */
    public abstract int getBodyLength20();

    /**
     * 把header也拷贝一份，本身clone是浅拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public AbstractCmppMessage clone() throws CloneNotSupportedException {
        AbstractCmppMessage abstractCmppMessage = (AbstractCmppMessage) super.clone();
        CmppHeader cmppHeader = this.header.clone();
        abstractCmppMessage.setHeader(cmppHeader);
        return abstractCmppMessage;
    }

    @Override
    public int getSequenceId() {
        return getHeader().getSequenceId();
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
        return ++retryCount;
    }

    @Override
    public void getRetryCount() {
        return;
    }

    @Override
    public boolean isRequest() {
        return getHeader().isRequest();
    }

    @Override
    public void setRequestMessage(IMessage iMessage) {
        this.requestMessage = iMessage;
    }

    @Override
    public IMessage getRequestMessage() {
        return this.requestMessage;
    }

    @Override
    public void setSendTimeStamp(long timeStamp) {
        this.sendTimestamp = timeStamp;
    }

    @Override
    public long getSendTimeStamp() {
        return sendTimestamp;
    }

    @Override
    public void handleMessageComplete(IMessage request, IMessage response) {
        IMessageResponseHandler messageResponseHandler = getMessageResponseHandler();
        if (messageResponseHandler != null) {
            messageResponseHandler.messageComplete(request, response);
        }
    }

    @Override
    public void handleMessageSendFailed(IMessage request) {
        IMessageResponseHandler messageResponseHandler = getMessageResponseHandler();
        if (messageResponseHandler != null) {
            messageResponseHandler.sendMessageFailed(request);
        }
    }

    @Override
    public void handleMessageExpired(String cachedKey,IMessage request) {
        IMessageResponseHandler messageCompleteHandler = getMessageResponseHandler();
        if (messageCompleteHandler != null) {
            messageCompleteHandler.messageExpired(cachedKey, request);
        }
    }

    public IMessageResponseHandler getMessageResponseHandler() {
        return messageResponseHandler;
    }

    @Override
    public void setMessageResponseHandler(IMessageResponseHandler messageResponseHandler) {
        this.messageResponseHandler = messageResponseHandler;
    }

    @Override
    public boolean isActiveTestMessage() {
        return false;
    }
}
