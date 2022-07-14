package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: SmppEnquireLinkRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppEnquireLinkResponseMessage extends AbstractSmppMessage {

    public SmppEnquireLinkResponseMessage() {
        super(SmppPackageType.ENQUIRELINKRESPONSE);
    }

    public SmppEnquireLinkResponseMessage(SmppHeader header) {
        super(SmppPackageType.ENQUIRELINKRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public boolean isActiveTestMessage() {
        return true;
    }

    @Override
    public String toString() {
        return "SmppEnquireLinkResponseMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
