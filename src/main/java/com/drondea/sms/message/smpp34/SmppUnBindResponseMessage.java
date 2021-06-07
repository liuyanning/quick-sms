package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: SmppUnBindRequestMessage响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppUnBindResponseMessage extends AbstractSmppMessage {


    public SmppUnBindResponseMessage() {
        super(SmppPackageType.UNBINDRESPONSE);
    }

    public SmppUnBindResponseMessage(SmppHeader header) {
        super(SmppPackageType.UNBINDRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "SmppUnBindResponseMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
