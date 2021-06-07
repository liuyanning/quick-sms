package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.common.util.SmgpMsgId;

public class SmgpDeliverResponseMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = 928417176516979508L;

    public SmgpDeliverResponseMessage() {
        super(SmgpPackageType.DELIVERRESPONSE);
    }

    public SmgpDeliverResponseMessage(SmgpHeader header) {
        super(SmgpPackageType.DELIVERRESPONSE, header);
    }

    private SmgpMsgId msgId; // 10

    private int status; // 4

    public SmgpMsgId getSmgpMsgId() {
        return this.msgId;
    }

    public void setSmgpMsgId(SmgpMsgId msgId) {
        this.msgId = msgId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private String msgIdString() {
        return msgId.toString();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpDeliverResponseMessage:[sequenceNumber=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("commandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("msgId=").append(msgIdString()).append(",");
        buffer.append("status=").append(status);
        buffer.append("]");
        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        return 14;
    }

    @Override
    public boolean isWindowResponseMessage() {
        return true;
    }
}