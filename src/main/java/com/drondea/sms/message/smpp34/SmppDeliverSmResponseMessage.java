package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppDeliverSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppDeliverSmResponseMessage extends AbstractSmppMessage {

    public SmppDeliverSmResponseMessage() {
        super(SmppPackageType.DELIVERSMRESPONSE);
    }

    public SmppDeliverSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.DELIVERSMRESPONSE, header);
    }

    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public int getBodyLength() {
        return getStringLengthPlusOne(this.messageId);
    }

    @Override
    public boolean isWindowResponseMessage() {
        return true;
    }

    @Override
    public String toString() {
        return "SmppDeliverSmResponseMessage{" +
                "header=" + this.getHeader().toString() +
                "messageId='" + messageId + '\'' +
//                "OptionalParameters='" + getOptionalParameters().toString() + '\'' +
                '}';
    }
}
