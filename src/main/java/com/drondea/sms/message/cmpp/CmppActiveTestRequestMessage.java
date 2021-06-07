package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: 心跳包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:49
 **/
public class CmppActiveTestRequestMessage extends AbstractCmppMessage {

    public CmppActiveTestRequestMessage() {
        super(CmppPackageType.ACTIVETESTREQUEST);
    }

    public CmppActiveTestRequestMessage(CmppHeader header) {
        super(CmppPackageType.ACTIVETESTREQUEST, header);
    }

    @Override
    public int getBodyLength30() {
        return 0;
    }

    @Override
    public int getBodyLength20() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("CmppActiveTestRequestMessage [Header()=%s]",
                getHeader().toString());
    }
}
