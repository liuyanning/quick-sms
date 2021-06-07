package com.drondea.sms.message.smpp34;


import java.util.Arrays;

import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: submit_multi操作是submit_sm的一个增强变体，
 * 旨在支持多达255个不同的目的地，而不是默认的单一目的地。
 * 它提供了一种有效的方法，可以同时将相同的消息发送给几个不同的订阅者
 * submit_sm PDU的一个变体，对给定的消息最多支持255个接收方
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppSubmitMultiRequestMessage extends AbstractSmppMessage {

    public SmppSubmitMultiRequestMessage() {
        super(SmppPackageType.SUBMITMULTIREQUEST);
    }

    public SmppSubmitMultiRequestMessage(SmppHeader header) {
        super(SmppPackageType.SUBMITMULTIREQUEST, header);
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

    /**
     * 目的地地址数目-指示接下来要到达的目的地数目。最多允许255个目的地址。
     * 注:当提交一个SME地址或提交一个分发时，设置为1列表
     */
    private short numberOfDests;

    ///SME格式目的地地址(复合字段)
    private short destFlag;
    private short destAddrTon;
    private short destAddrNpi;
    private String destinationAddr;

    //分配表的格式目的地址(复合字段)
    private short dlDestFlag;
    private String dlName;

    private short esmClass;
    private short protocolId;
    private short priorityFlag;

    private String scheduleDeliveryTime;
    private String validityPeriod;
    private short registeredDelivery;
    private short replaceIfPresentFlag;
    private short dataCoding;
    private short smDefaultMsgIid;
    private short smLength;
    private byte[] shortMessage;

    @Override
    public int getBodyLength() {
        int bodyLength = 15; //各个short类型
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.destinationAddr);
        bodyLength += getStringLengthPlusOne(this.dlName);
        bodyLength += getStringLengthPlusOne(this.scheduleDeliveryTime);
        bodyLength += getStringLengthPlusOne(this.validityPeriod);
        bodyLength += this.shortMessage == null ? 0 : this.shortMessage.length;
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

    public short getNumberOfDests() {
        return numberOfDests;
    }

    public void setNumberOfDests(short numberOfDests) {
        this.numberOfDests = numberOfDests;
    }

    public short getDestFlag() {
        return destFlag;
    }

    public void setDestFlag(short destFlag) {
        this.destFlag = destFlag;
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

    public short getDlDestFlag() {
        return dlDestFlag;
    }

    public void setDlDestFlag(short dlDestFlag) {
        this.dlDestFlag = dlDestFlag;
    }

    public String getDlName() {
        return dlName;
    }

    public void setDlName(String dlName) {
        this.dlName = dlName;
    }

    public short getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(short esmClass) {
        this.esmClass = esmClass;
    }

    public short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(short protocolId) {
        this.protocolId = protocolId;
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

    public short getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
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

    public short getSmLength() {
        return smLength;
    }

    public void setSmLength(short smLength) {
        this.smLength = smLength;
    }

    public byte[] getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(byte[] shortMessage) {
        this.shortMessage = shortMessage;
    }

    @Override
    public String toString() {
        return "SmppSubmitMultiRequestMessage{" +
                "header='" + getHeader().toString() + '\'' +
                "serviceType='" + serviceType + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", numberOfDests=" + numberOfDests +
                ", destFlag=" + destFlag +
                ", destAddrTon=" + destAddrTon +
                ", destAddrNpi=" + destAddrNpi +
                ", destinationAddr='" + destinationAddr + '\'' +
                ", dlDestFlag=" + dlDestFlag +
                ", dlName='" + dlName + '\'' +
                ", esmClass=" + esmClass +
                ", protocolId=" + protocolId +
                ", priorityFlag=" + priorityFlag +
                ", scheduleDeliveryTime='" + scheduleDeliveryTime + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", registeredDelivery=" + registeredDelivery +
                ", replaceIfPresentFlag=" + replaceIfPresentFlag +
                ", dataCoding=" + dataCoding +
                ", smDefaultMsgIid=" + smDefaultMsgIid +
                ", smLength=" + smLength +
                ", shortMessage=" + Arrays.toString(shortMessage) +
                '}';
    }
}
