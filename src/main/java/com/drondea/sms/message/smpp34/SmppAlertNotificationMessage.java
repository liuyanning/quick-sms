package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: MC向ESME发送alert_notification，以提醒其SME的可用性
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppAlertNotificationMessage extends AbstractSmppMessage {

    public SmppAlertNotificationMessage() {
        super(SmppPackageType.ALERTNOTIFICATION);
    }

    public SmppAlertNotificationMessage(SmppHeader header) {
        super(SmppPackageType.ALERTNOTIFICATION, header);
    }

    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private short esmeAddrTon;
    private short esmeAddrNpi;
    private String esmeAddr;


    @Override
    public int getBodyLength() {
        int bodyLength = 4; //各个short类型
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.esmeAddr);
        return bodyLength;
    }

    public short getSourceAddrTon() {
        return sourceAddrTon;
    }

    public void setSourceAddrTon(short sourceAddrTon) {
        this.sourceAddrTon = sourceAddrTon;
    }

    public short getSourceAddrNpi() {
        return sourceAddrNpi;
    }

    public void setSourceAddrNpi(short sourceAddrNpi) {
        this.sourceAddrNpi = sourceAddrNpi;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public short getEsmeAddrTon() {
        return esmeAddrTon;
    }

    public void setEsmeAddrTon(short esmeAddrTon) {
        this.esmeAddrTon = esmeAddrTon;
    }

    public short getEsmeAddrNpi() {
        return esmeAddrNpi;
    }

    public void setEsmeAddrNpi(short esmeAddrNpi) {
        this.esmeAddrNpi = esmeAddrNpi;
    }

    public String getEsmeAddr() {
        return esmeAddr;
    }

    public void setEsmeAddr(String esmeAddr) {
        this.esmeAddr = esmeAddr;
    }

    @Override
    public String toString() {
        return "SmppAlertNotificationMessage{" +
                "header=" + this.getHeader().toString() +
                "sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", esmeAddrTon=" + esmeAddrTon +
                ", esmeAddrNpi=" + esmeAddrNpi +
                ", esmeAddr='" + esmeAddr + '\'' +
                "OptionalParameters='" + getOptionalParameters().toString() + '\'' +
                '}';
    }
}
