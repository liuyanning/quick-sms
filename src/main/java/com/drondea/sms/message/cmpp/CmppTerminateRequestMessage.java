package com.drondea.sms.message.cmpp;

import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: 断开连接请求
 * @author: 刘彦宁
 * @date: 2020年06月17日17:08
 **/
public class CmppTerminateRequestMessage extends AbstractCmppMessage {

    public CmppTerminateRequestMessage() {
        super(CmppPackageType.TERMINATEREQUEST);
    }

    public CmppTerminateRequestMessage(CmppHeader header) {
        super(CmppPackageType.TERMINATEREQUEST, header);
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
        return "CmppTerminateRequestMessage [toString()= "+ super.toString() +" ]";
    }
}
