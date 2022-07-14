package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: 该PDU用于取消先前广播消息的状态
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppCancelBroadcastSmRequestMessage extends AbstractSmppMessage {

    public SmppCancelBroadcastSmRequestMessage() {
        super(SmppPackageType.CANCELBROADCASTSMREQUEST);
    }

    public SmppCancelBroadcastSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.CANCELBROADCASTSMREQUEST, header);
    }

    /**
     * 设置为CBS应用程序服务(如果需要取消一组应用程序服务消息)。
     * 否则设置为空。
     */
    private String serviceType;
    private String messageId;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    @Override
    public int getBodyLength() {
        int bodyLength = 2; //各个short类型
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        return bodyLength;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public short getSourceAddrTon() {
        return sourceAddrTon;
    }

    public void setSourceAddrTon(short sourceAddrTon) {
        this.sourceAddrTon = sourceAddrTon;
    }

    public short getSourceAddrNpi() {
        return sourceAddrNpi;
    }

    public void setSourceAddrNpi(short sourceAddrNpi) {
        this.sourceAddrNpi = sourceAddrNpi;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    @Override
    public String toString() {
        return "SmppCancelBroadcastSmRequestMessage{" +
                ", serviceType='" + serviceType + '\'' +
                ", messageId='" + messageId + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                '}';
    }
}
