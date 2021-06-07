package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: SmppReplaceSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppCancelBroadcastSmResponseMessage extends AbstractSmppMessage {

    public SmppCancelBroadcastSmResponseMessage() {
        super(SmppPackageType.CANCELBROADCASTSMRESPONSE);
    }

    public SmppCancelBroadcastSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.CANCELBROADCASTSMRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "SmppReplaceSmResponseMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
