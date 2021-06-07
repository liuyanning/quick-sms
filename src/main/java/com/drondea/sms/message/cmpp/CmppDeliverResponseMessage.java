package com.drondea.sms.message.cmpp;

import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: deliver的响应包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:50
 **/
public class CmppDeliverResponseMessage extends AbstractCmppMessage {

    private MsgId msgId;
    private long result = 0;

    public CmppDeliverResponseMessage() {
        super(CmppPackageType.DELIVERRESPONSE);
    }

    public CmppDeliverResponseMessage(CmppHeader header) {
        super(CmppPackageType.DELIVERRESPONSE, header);
    }

    @Override
    public int getBodyLength30() {
        return 12;
    }

    @Override
    public int getBodyLength20() {
        return 9;
    }

    @Override
    public boolean isWindowResponseMessage() {
        return true;
    }


    public MsgId getMsgId() {
        return msgId;
    }

    public void setMsgId(MsgId msgId) {
        this.msgId = msgId;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CmppDeliverResponseMessage [msgId=").append(msgId).append(", result=").append(result).append(", sequenceId=")
                .append(getHeader().getSequenceId()).append( "]");
        return sb.toString();
    }
}
