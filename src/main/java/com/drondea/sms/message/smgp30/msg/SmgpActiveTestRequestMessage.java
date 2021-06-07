package com.drondea.sms.message.smgp30.msg;


public class SmgpActiveTestRequestMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = -5589548347716994701L;

    public SmgpActiveTestRequestMessage() {
        super(SmgpPackageType.ACTIVETESTREQUEST);
    }

    public SmgpActiveTestRequestMessage(SmgpHeader header) {
        super(SmgpPackageType.ACTIVETESTREQUEST, header);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpActiveTestRequestMessage:[sequenceNumber=")
                .append(getHeader().getSequenceId()).append("]");
        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
}