package com.drondea.sms.message.smgp30.msg;

public class SmgpExitRequestMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = -8459840003695284990L;

    public SmgpExitRequestMessage() {
        super(SmgpPackageType.TERMINATEREQUEST);
    }

    public SmgpExitRequestMessage(SmgpHeader header) {
        super(SmgpPackageType.TERMINATEREQUEST, header);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpTerminateRequestMessage:[sequenceNumber=")
                .append(getSequenceId()).append("]");
        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
}