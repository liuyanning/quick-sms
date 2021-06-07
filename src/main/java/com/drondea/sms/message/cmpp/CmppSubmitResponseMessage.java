package com.drondea.sms.message.cmpp;

import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.cmpp30.CmppPackageType;

/**
 * @version V3.0.0
 * @description: 提交短信响应包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:49
 **/
public class CmppSubmitResponseMessage extends AbstractCmppMessage {

    private MsgId msgId;
    private long result = 0;

    public CmppSubmitResponseMessage() {
        super(CmppPackageType.SUBMITRESPONSE);
    }

    public CmppSubmitResponseMessage(CmppHeader header) {
        super(CmppPackageType.SUBMITRESPONSE, header);
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
        sb.append("CmppSubmitResponseMessage [msgId=").append(msgId).append(", result=").append(result).append(", sequenceId=")
                .append(getHeader().getSequenceId()).append("]");
        return sb.toString();
    }
}
