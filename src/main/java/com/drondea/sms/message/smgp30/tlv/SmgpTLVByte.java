package com.drondea.sms.message.smgp30.tlv;


public class SmgpTLVByte extends SmgpTLV {
    private byte value = 0;

    public SmgpTLVByte() {
        super(1, 1);
    }

    public SmgpTLVByte(short p_tag) {
        super(p_tag, 1, 1);
    }

    public SmgpTLVByte(short p_tag, byte p_value) {
        super(p_tag, 1, 1);
        value = p_value;
        markValueSet();
    }

    public void setValue(byte p_value) {
        value = p_value;
        markValueSet();
    }

    public byte getValue() {
        if (hasValue()) {
            return value;
        } else {
            return 0;
        }
    }

    @Override
    public byte[] getValueData() throws Exception {

        return new byte[]{getValue()};
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        setValue(buffer[0]);
        markValueSet();
    }

}