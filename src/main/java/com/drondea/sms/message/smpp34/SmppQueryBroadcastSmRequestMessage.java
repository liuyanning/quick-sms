package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: 这个PDU用来查询先前广播消息的状态。
 * PDU包含原始消息的源地址和原始broadcast_sm_resp PDU中返回的message_id
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppQueryBroadcastSmRequestMessage extends AbstractSmppMessage {

    public SmppQueryBroadcastSmRequestMessage() {
        super(SmppPackageType.QUERYBROADCASTSMREQUEST);
    }

    public SmppQueryBroadcastSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.QUERYBROADCASTSMREQUEST, header);
    }

    private String messageId;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;


    @Override
    public int getBodyLength() {
        //各个short类型
        int bodyLength = 2;
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
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

    @Override
    public String toString() {
        return "SmppQueryBroadcastSmRequestMessage{" +
                "messageId='" + messageId + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                '}';
    }
}
