package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppBroadcastSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppBroadcastSmResponseMessage extends AbstractSmppMessage {

    public SmppBroadcastSmResponseMessage() {
        super(SmppPackageType.BROADCASTSMRESPONSE);
    }

    public SmppBroadcastSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.BROADCASTSMRESPONSE, header);
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
        return "SmppBroadcastSmResponseMessage{" +
                "messageId='" + messageId + '\'' +
                '}';
    }
}
