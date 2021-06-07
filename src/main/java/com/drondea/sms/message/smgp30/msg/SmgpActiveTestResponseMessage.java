package com.drondea.sms.message.smgp30.msg;


public class SmgpActiveTestResponseMessage extends AbstractSmgpMessage {

    /**
     *
     */
    private static final long serialVersionUID = 5971146145881161402L;

    public SmgpActiveTestResponseMessage() {
        super(SmgpPackageType.ACTIVETESTRESPONSE);
    }

    public SmgpActiveTestResponseMessage(SmgpHeader header) {
        super(SmgpPackageType.ACTIVETESTRESPONSE, header);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpActiveTestResponseMessage:[sequenceNumber=").append(
                getSequenceId()).append("]");

        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
}