package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppQuerySmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppQuerySmResponseMessage extends AbstractSmppMessage {

    public SmppQuerySmResponseMessage() {
        super(SmppPackageType.QUERYSMRESPONSE);
    }

    public SmppQuerySmResponseMessage(SmppHeader header) {
        super(SmppPackageType.QUERYSMRESPONSE, header);
    }

    private String messageId;
    private String finalDate;
    private short messageState;
    private short errorCode;

    @Override
    public int getBodyLength() {
        int bodyLength = 2; //各个short类型
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.finalDate);
        return bodyLength;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public short getMessageState() {
        return messageState;
    }

    public void setMessageState(short messageState) {
        this.messageState = messageState;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "SmppQuerySmResponseMessage{" +
                "messageId='" + messageId + '\'' +
                ", finalDate='" + finalDate + '\'' +
                ", messageState=" + messageState +
                ", errorCode=" + errorCode +
                '}';
    }
}
