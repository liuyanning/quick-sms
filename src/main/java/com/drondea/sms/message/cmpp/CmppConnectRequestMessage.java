package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: cmpp连接请求包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:34
 **/
public class CmppConnectRequestMessage extends AbstractCmppMessage {

    /**
     * 源地址，此处为SP_Id，即SP的企业代码。6字节
     */
    private String sourceAddr = "";
    /**
     * 鉴权字段，16字节
     */
    private byte[] authenticatorSource;
    /**
     * 版本号，1字节
     */
    private short version = 0x30;
    /**
     * 时间戳，4字节
     */
    private long timestamp = 0L;

    public CmppConnectRequestMessage() {
        super(CmppPackageType.CONNECTREQUEST);
    }

    public CmppConnectRequestMessage(CmppHeader header) {
        super(CmppPackageType.CONNECTREQUEST, header);
    }


    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public byte[] getAuthenticatorSource() {
        return authenticatorSource;
    }

    public void setAuthenticatorSource(byte[] authenticatorSource) {
        this.authenticatorSource = authenticatorSource;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public int getBodyLength30() {
        return 27;
    }

    @Override
    public int getBodyLength20() {
        return 27;
    }

    @Override
    public String toString() {
        return "CmppConnectRequestMessage [version="+ version +
                ", sourceAddr= "+ sourceAddr +", sequenceId= "+ getHeader().getSequenceId() +"]";
    }

}
