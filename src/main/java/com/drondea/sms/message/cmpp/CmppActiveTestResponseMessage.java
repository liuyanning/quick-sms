package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: 心跳检测回应
 * @author: 刘彦宁
 * @date: 2020年06月08日10:50
 **/
public class CmppActiveTestResponseMessage extends AbstractCmppMessage {
    /**
     * 保留字段，1个字节
     */
    private short reserved = 0;

    public CmppActiveTestResponseMessage() {
        super(CmppPackageType.ACTIVETESTRESPONSE);
    }

    public CmppActiveTestResponseMessage(CmppHeader header) {
        super(CmppPackageType.ACTIVETESTRESPONSE, header);
    }

    @Override
    public int getBodyLength30() {
        return 1;
    }

    @Override
    public int getBodyLength20() {
        return 1;
    }


    public short getReserved() {
        return reserved;
    }

    public void setReserved(short reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return String.format("CmppActiveTestResponseMessage [Header()=%s]",
                getHeader().toString());
    }
}
