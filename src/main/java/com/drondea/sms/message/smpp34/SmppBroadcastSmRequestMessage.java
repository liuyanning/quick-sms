package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: 一个广播的ESME，希望广播一个短消息，可以使用这个PDU来指定别名，地理区域，和短消息的文本
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppBroadcastSmRequestMessage extends AbstractSmppMessage {

    public SmppBroadcastSmRequestMessage() {
        super(SmppPackageType.BROADCASTSMREQUEST);
    }

    public SmppBroadcastSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.BROADCASTSMREQUEST, header);
    }

    /**
     * service_type参数可用于指示SMS与消息关联的应用程序服务。指定service_type允许
     * ESME使用增强的消息传递服务，如“替换为service_type”或控制空中接口上使用的teleservice。
     * 默认MC设置为NULL
     */
    private String serviceType;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private String messageId;
    private short priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private short replaceIfPresentFlag;
    private short dataCoding;
    private short smDefaultMsgIid;

    @Override
    public int getBodyLength() {
        int bodyLength = 6; //各个short类型
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.scheduleDeliveryTime);
        bodyLength += getStringLengthPlusOne(this.validityPeriod);
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public short getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(short priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public short getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    public void setReplaceIfPresentFlag(short replaceIfPresentFlag) {
        this.replaceIfPresentFlag = replaceIfPresentFlag;
    }

    public short getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(short dataCoding) {
        this.dataCoding = dataCoding;
    }

    public short getSmDefaultMsgIid() {
        return smDefaultMsgIid;
    }

    public void setSmDefaultMsgIid(short smDefaultMsgIid) {
        this.smDefaultMsgIid = smDefaultMsgIid;
    }

    @Override
    public String toString() {
        return "SmppBroadcastSmRequestMessage{" +
                ", serviceType='" + serviceType + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", messageId='" + messageId + '\'' +
                ", priorityFlag=" + priorityFlag +
                ", scheduleDeliveryTime='" + scheduleDeliveryTime + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", replaceIfPresentFlag=" + replaceIfPresentFlag +
                ", dataCoding=" + dataCoding +
                ", smDefaultMsgIid=" + smDefaultMsgIid +
                '}';
    }
}
