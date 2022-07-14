package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;
import com.drondea.sms.type.CmppConstants;
import org.apache.commons.codec.binary.Hex;

/**
 * @version V3.0.0
 * @description: cmpp连接请求回应包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:41
 **/
public class CmppConnectResponseMessage extends AbstractCmppMessage {

    /**
     * 请求登录返回状态，4字节
     */
    private long status = 3;
    private byte[] authenticatorISMG;
    private short version = 0x30;

    public CmppConnectResponseMessage() {
        super(CmppPackageType.CONNECTRESPONSE);
    }

    public CmppConnectResponseMessage(CmppHeader header) {
        super(CmppPackageType.CONNECTRESPONSE, header);
    }

    @Override
    public int getBodyLength30() {
        return 21;
    }

    @Override
    public int getBodyLength20() {
        return 18;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public byte[] getAuthenticatorISMG() {
        return authenticatorISMG;
    }

    public void setAuthenticatorISMG(byte[] authenticatorISMG) {
        this.authenticatorISMG = authenticatorISMG;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
        if (version == CmppConstants.VERSION_20) {
            getHeader().setBodyLength(getBodyLength20());
        } else {
            getHeader().setBodyLength(getBodyLength30());
        }
    }
    @Override
    public String toString() {
        return "CmppConnectResponseMessage [version="+ version +
                ",status="+ status +",authenticatorISMG= "+ status +
                ",sequenceId= "+ getHeader().getSequenceId() +"]";
    }
}
