package com.drondea.sms.message.smgp30.msg;


public class SmgpTerminateResponseMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = -4438794269709080555L;

    public SmgpTerminateResponseMessage() {
        super(SmgpPackageType.TERMINATERESPONSE);
    }

    public SmgpTerminateResponseMessage(SmgpHeader header) {
        super(SmgpPackageType.TERMINATERESPONSE, header);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpTerminateResponseMessage:[sequenceNumber=").append(
                getSequenceId()).append(",");
        buffer.append("]");
        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
}