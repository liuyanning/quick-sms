package com.drondea.sms.message.smgp30.msg;


public class SmgpExitResponseMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = -4438794269709080555L;

    public SmgpExitResponseMessage() {
        super(SmgpPackageType.TERMINATERESPONSE);
    }

    public SmgpExitResponseMessage(SmgpHeader header) {
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