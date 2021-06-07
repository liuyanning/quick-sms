package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: 这个PDU可以由ESME或MC发送，作为启动SMPP会话终止的一种方式。
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppUnBindRequestMessage extends AbstractSmppMessage {

    public SmppUnBindRequestMessage() {
        super(SmppPackageType.UNBINDREQUEST);
    }

    public SmppUnBindRequestMessage(SmppHeader header) {
        super(SmppPackageType.UNBINDREQUEST, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public String toString() {
        return "SmppUnBindRequestMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
