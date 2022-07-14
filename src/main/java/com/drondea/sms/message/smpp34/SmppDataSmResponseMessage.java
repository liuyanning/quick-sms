package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppDataSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppDataSmResponseMessage extends AbstractSmppMessage {

    public SmppDataSmResponseMessage() {
        super(SmppPackageType.DATASMRESPONSE);
    }

    public SmppDataSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.DATASMRESPONSE, header);
    }

    private String messageId;

    @Override
    public int getBodyLength() {
        return getStringLengthPlusOne(this.messageId);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "SmppDataSmResponseMessage{" +
                "header=" + this.getHeader().toString() +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
