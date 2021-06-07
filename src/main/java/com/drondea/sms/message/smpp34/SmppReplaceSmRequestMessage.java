package com.drondea.sms.message.smpp34;


import java.util.Arrays;

import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: ESME使用replace_sm PDU传递先前提交的消息的message_id以及用于更新消息的文本，有效期和其他属性的其他几个字段
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppReplaceSmRequestMessage extends AbstractSmppMessage {

    public SmppReplaceSmRequestMessage() {
        super(SmppPackageType.REPLACESMREQUEST);
    }

    public SmppReplaceSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.REPLACESMREQUEST, header);
    }

    private String messageId;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private String scheduleDeliveryTime;
    private String validityPeriod;
    private short registeredDelivery;
    private short smDefaultMsgIid;
    private short smLength;
    private byte[] shortMessage;

    @Override
    public int getBodyLength() {
        int bodyLength = 5; //各个short类型
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.scheduleDeliveryTime);
        bodyLength += getStringLengthPlusOne(this.validityPeriod);
        bodyLength += this.shortMessage == null ? 0 : this.shortMessage.length;
        return bodyLength;
    }

    @Override
    public boolean isRequest() {
        return true;
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
        return "SmppReplaceSmRequestMessage{" +
                "messageId='" + messageId + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", scheduleDeliveryTime='" + scheduleDeliveryTime + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", registeredDelivery=" + registeredDelivery +
                ", smDefaultMsgIid=" + smDefaultMsgIid +
                ", smLength=" + smLength +
                ", shortMessage=" + Arrays.toString(shortMessage) +
                '}';
    }
}
