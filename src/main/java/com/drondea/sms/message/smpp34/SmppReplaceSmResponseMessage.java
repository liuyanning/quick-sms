package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: SmppReplaceSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppReplaceSmResponseMessage extends AbstractSmppMessage {

    public SmppReplaceSmResponseMessage() {
        super(SmppPackageType.REPLACESMRESPONSE);
    }

    public SmppReplaceSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.REPLACESMRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "SmppReplaceSmRequestMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
