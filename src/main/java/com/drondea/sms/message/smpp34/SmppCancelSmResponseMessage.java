package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: SmppCancelSmRequestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppCancelSmResponseMessage extends AbstractSmppMessage {

    public SmppCancelSmResponseMessage() {
        super(SmppPackageType.CANCELSMRESPONSE);
    }

    public SmppCancelSmResponseMessage(SmppHeader header) {
        super(SmppPackageType.CANCELSMRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "SmppCancelSmResponseMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
