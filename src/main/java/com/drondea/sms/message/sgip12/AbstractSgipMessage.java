package com.drondea.sms.message.sgip12;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.type.IMessageResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: Sgip包父类
 * @author: liyuehai
 * @date: 2020年06月08日14:23
 **/
public abstract class AbstractSgipMessage implements IMessage, Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSgipMessage.class);

    private IPackageType packageType;
    /**
     * 消息的生命周期，单位秒, 0表示永不过期
     */
    private long lifeTime = 0;
    private SgipHeader header;

    /**
     * 发送重试次数
     */
    private int retryCount = 0;

    private IMessageResponseHandler messageResponseHandler;
    private IMessage requestMessage;
    private long sendTimestamp;

    public AbstractSgipMessage(IPackageType packageType) {
        setPackageType(packageType);
        SgipHeader header = new SgipHeader(packageType);
        header.setCommandId(packageType.getCommandId());
        setHeader(header);
    }

    public AbstractSgipMessage(IPackageType packageType, SgipHeader header) {
        setPackageType(packageType);
        if (header == null) {
            logger.error("sgip header cannot be null");
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

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public SgipHeader getHeader() {
        return header;
    }

    public void setHeader(SgipHeader header) {
        this.header = header;
    }

    public int getHeaderLength() {
        return getHeader().getHeadLength();
    }

    /**
     * 获取消息的body长度
     *
     * @return
     */
    public abstract int getBodyLength();

    /**
     * 把header也拷贝一份，本身clone是浅拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public AbstractSgipMessage clone() throws CloneNotSupportedException {
        AbstractSgipMessage abstractSgipMessage = (AbstractSgipMessage) super.clone();
        SgipHeader sgipHeader = this.header.clone();
        abstractSgipMessage.setHeader(sgipHeader);
        return abstractSgipMessage;
    }

    @Override
    public int getSequenceId() {
        return getHeader().getSequenceNumber().getSequenceId();
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

    }

    @Override
    public boolean isRequest() {
        return getHeader().isRequest();
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
    public void setRequestMessage(IMessage iMessage) {
        this.requestMessage = iMessage;
    }

    @Override
    public IMessage getRequestMessage() {
        return this.requestMessage;
    }

    @Override
    public void handleMessageExpired(String cachedKey, IMessage request) {
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
    public void setSendTimeStamp(long timeStamp) {
        this.sendTimestamp = timeStamp;
    }

    @Override
    public long getSendTimeStamp() {
        return sendTimestamp;
    }

    @Override
    public boolean isActiveTestMessage() {
        return false;
    }
}
