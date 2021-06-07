package com.drondea.sms.message.smgp30.tlv;


public class SmgpTLVEmpty extends SmgpTLV {
    private boolean present = false;

    public SmgpTLVEmpty() {
        super(0, 0);
    }

    public SmgpTLVEmpty(short p_tag) {
        super(p_tag, 0, 0);
    }

    public SmgpTLVEmpty(short p_tag, boolean p_present) {
        super(p_tag, 0, 0);
        present = p_present;
        markValueSet();
    }

    public void setValue(boolean p_present) {
        present = p_present;
        markValueSet();
    }

    public boolean getValue() {
        return present;

    }

    @Override
    public byte[] getValueData() throws Exception {
        return null;
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        setValue(true);
        markValueSet();
    }

}