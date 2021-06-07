package com.drondea.sms.message.smgp30.tlv;


public class SmgpTLVOctets extends SmgpTLV {
    private byte[] value = null;

    public SmgpTLVOctets() {
        super();
    }

    public SmgpTLVOctets(short p_tag) {
        super(p_tag);
    }

    public SmgpTLVOctets(short p_tag, int min, int max) {
        super(p_tag, min, max);
    }

    public SmgpTLVOctets(short p_tag, byte[] p_value) throws Exception {
        super(p_tag);
        setValueData(p_value);
    }

    public SmgpTLVOctets(short p_tag, int min, int max, byte[] p_value) throws Exception {
        super(p_tag, min, max);
        setValueData(p_value);
    }

    public void setValue(byte[] p_value) {
        value = p_value;
        markValueSet();
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public byte[] getValueData() throws Exception {
        return value;
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        value = buffer;
        markValueSet();

    }

}
