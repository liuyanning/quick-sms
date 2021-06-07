package com.drondea.sms.thirdparty;


public class SmsUnkownTypeMessage implements SmsMessage {

    private byte[] ud;
    private byte dcs;

    public SmsUnkownTypeMessage(byte dcs, byte[] ud) {
        this.ud = ud;
        this.dcs = dcs;
    }

    @Override
    public SmsPdu[] getPdus() {
        SmsUserData sud = new SmsUserData(ud, ud.length, new SmsDcs(dcs));
        return new SmsPdu[]{new SmsPdu(new SmsUdhElement[]{}, sud)};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }


}
