package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: MMC使用此操作向ESME发出信号，让它向MC发起一个outbind请求。
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppOutBindMessage extends AbstractSmppMessage {

    public SmppOutBindMessage() {
        super(SmppPackageType.OUTBIND);
    }

    public SmppOutBindMessage(SmppHeader header) {
        super(SmppPackageType.OUTBIND, header);
    }

    private String systemId;
    private String password;


    @Override
    public int getBodyLength() {
        return getStringLengthPlusOne(systemId) + getStringLengthPlusOne(password);
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

    @Override
    public String toString() {
        return "SmppOutBindMessage{" +
                "header=" + this.getHeader().toString() +
                ", systemId='" + systemId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
