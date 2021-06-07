package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: smpp协议的 BindTransceiver 绑定为收发器
 * @author: gengjinbiao
 * @date: 2020年07月03日10:29
 **/
public class SmppBindTransceiverRequestMessage extends AbstractSmppMessage {

    private String systemId;
    private String password;
    private String systemType;
    private short interfaceVersion;
    private short addrTon;
    private short addrNpi;
    private String addressRange;

    public SmppBindTransceiverRequestMessage() {
        super(SmppPackageType.BINDTRANSCEIVERREQUEST);
    }

    public SmppBindTransceiverRequestMessage(SmppHeader header) {
        super(SmppPackageType.BINDTRANSCEIVERREQUEST, header);
    }

    @Override
    public int getBodyLength() {
        int bodyLength = 3;
        bodyLength += getStringLengthPlusOne(this.systemId);
        bodyLength += getStringLengthPlusOne(this.password);
        bodyLength += getStringLengthPlusOne(this.systemType);
        bodyLength += getStringLengthPlusOne(this.addressRange);
        return bodyLength;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public short getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(short interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public short getAddrTon() {
        return addrTon;
    }

    public void setAddrTon(short addrTon) {
        this.addrTon = addrTon;
    }

    public short getAddrNpi() {
        return addrNpi;
    }

    public void setAddrNpi(short addrNpi) {
        this.addrNpi = addrNpi;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    @Override
    public String toString() {
        return "SmppBindTransceiverMessage{" +
                "header=" + this.getHeader().toString() +
                ", systemId='" + systemId + '\'' +
                ", password='" + password + '\'' +
                ", systemType='" + systemType + '\'' +
                ", interfaceVersion=" + interfaceVersion +
                ", addrTon=" + addrTon +
                ", addrNpi=" + addrNpi +
                ", addressRange='" + addressRange + '\'' +
                '}';
    }
}
