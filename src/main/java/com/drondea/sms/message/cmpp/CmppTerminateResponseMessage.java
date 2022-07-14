package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: 断开连接的响应
 * @author: 刘彦宁
 * @date: 2020年06月17日17:12
 **/
public class CmppTerminateResponseMessage extends AbstractCmppMessage {

    public CmppTerminateResponseMessage() {
        super(CmppPackageType.TERMINATERESPONSE);
    }

    public CmppTerminateResponseMessage(CmppHeader header) {
        super(CmppPackageType.TERMINATERESPONSE, header);
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
        return "CmppTerminateRequestMessage [Header= "+getHeader().toString()+"]";
    }
}
