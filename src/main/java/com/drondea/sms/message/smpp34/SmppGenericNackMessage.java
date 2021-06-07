package com.drondea.sms.message.smpp34;


/**
 * @version V3.0.0
 * @description: generic_nack PDU用于确认未识别或损坏的PDU提交。
 * 这个PDU可以由一个ESME或MC发送，作为一种指示接收无效PDU的方法。
 * 接收到generic_nack通常意味着远程对等点不能识别PDU，或者由于它的大小或内容而认为它是无效的PDU。
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppGenericNackMessage extends AbstractSmppMessage {

    public SmppGenericNackMessage() {
        super(SmppPackageType.GENERICNACK);
    }

    public SmppGenericNackMessage(SmppHeader header) {
        super(SmppPackageType.GENERICNACK, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "SmppGenericNackMessage{" +
                "header=" + this.getHeader().toString() +
                '}';
    }
}
