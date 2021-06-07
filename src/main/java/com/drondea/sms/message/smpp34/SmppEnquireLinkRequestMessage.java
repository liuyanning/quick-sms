package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: EPDU或MC可以发送此PDU来测试网络连接。期望接收对等方确认PDU作为验证测试​​的一种方式
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppEnquireLinkRequestMessage extends AbstractSmppMessage {

    public SmppEnquireLinkRequestMessage() {
        super(SmppPackageType.ENQUIRELINKREQUEST);
    }

    public SmppEnquireLinkRequestMessage(SmppHeader header) {
        super(SmppPackageType.ENQUIRELINKREQUEST, header);
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
        return "SmppEnquireLinkRequestMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
