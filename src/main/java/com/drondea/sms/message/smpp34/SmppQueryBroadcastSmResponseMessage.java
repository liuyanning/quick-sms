package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppQueryBroadcastSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppQueryBroadcastSmResponseMessage extends AbstractSmppMessage {

    public SmppQueryBroadcastSmResponseMessage() {
        super(SmppPackageType.QUERYBROADCASTSMRESPONSE);
    }

    public SmppQueryBroadcastSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.QUERYBROADCASTSMRESPONSE, header);
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
        return "SmppQueryBroadcastSmResponseMessage{" +
                "messageId='" + messageId + '\'' +
                '}';
    }
}
