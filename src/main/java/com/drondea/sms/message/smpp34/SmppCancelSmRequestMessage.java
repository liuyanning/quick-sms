package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: 这个PDU用来取消先前提交的消息。
 * PDU包含原始消息的源地址和原始submit_sm_resp、submit_multi_resp或data_sm_resp PDU中返回的message_id。
 * 这个PDU也可以省略message_id，而是包含一个源地址、目的地地址和可选的service_type字段，
 * 作为一种取消从一个地址发送到另一个地址的消息范围的方法
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppCancelSmRequestMessage extends AbstractSmppMessage {

    public SmppCancelSmRequestMessage() {
        super(SmppPackageType.CANCELSMREQUEST);
    }

    public SmppCancelSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.CANCELSMREQUEST, header);
    }

    /**
     * service_type参数可用于指示SMS与消息关联的应用程序服务。指定service_type允许
     * ESME使用增强的消息传递服务，如“替换为service_type”或控制空中接口上使用的teleservice。
     * 默认MC设置为NULL
     */
    private String serviceType;
    private String messageId;

    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private short destAddrTon;
    private short destAddrNpi;
    private String destinationAddr;

    @Override
    public int getBodyLength() {
        int bodyLength = 4; //各个short类型
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.destinationAddr);
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

    public short getDestAddrTon() {
        return destAddrTon;
    }

    public void setDestAddrTon(short destAddrTon) {
        this.destAddrTon = destAddrTon;
    }

    public short getDestAddrNpi() {
        return destAddrNpi;
    }

    public void setDestAddrNpi(short destAddrNpi) {
        this.destAddrNpi = destAddrNpi;
    }

    public String getDestinationAddr() {
        return destinationAddr;
    }

    public void setDestinationAddr(String destinationAddr) {
        this.destinationAddr = destinationAddr;
    }

    @Override
    public String toString() {
        return "SmppCancelSmRequestMessage{" +
                ", serviceType='" + serviceType + '\'' +
                ", messageId='" + messageId + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", destAddrTon=" + destAddrTon +
                ", destAddrNpi=" + destAddrNpi +
                ", destinationAddr='" + destinationAddr + '\'' +
                '}';
    }
}
