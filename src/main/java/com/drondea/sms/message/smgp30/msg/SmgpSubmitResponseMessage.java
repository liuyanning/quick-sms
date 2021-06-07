package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.common.util.SmgpMsgId;

/**
 * @version V3.0
 * @description: smgp提交短信响应包
 * @author: ywj
 * @date: 2020年06月08日10:49
 **/
public class SmgpSubmitResponseMessage extends AbstractSmgpMessage {

    private SmgpMsgId msgId;//10
    private long status = 0;//4

    public SmgpSubmitResponseMessage() {
        super(SmgpPackageType.SUBMITRESPONSE);
    }

    public SmgpSubmitResponseMessage(SmgpHeader header) {
        super(SmgpPackageType.SUBMITRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 14;
    }

    @Override
    public boolean isWindowResponseMessage() {
        return true;
    }


    public SmgpMsgId getSmgpMsgId() {
        return msgId;
    }

    public void setSmgpMsgId(SmgpMsgId msgId) {
        this.msgId = msgId;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpSubmitResponseMessage:[SequenceId=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("CommandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("SmgpMsgId=").append(getSmgpMsgId().toString()).append(",");
        buffer.append("Status=").append(getStatus()).append("]");
        return buffer.toString();
    }


}
