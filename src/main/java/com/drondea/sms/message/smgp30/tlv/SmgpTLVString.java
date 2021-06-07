package com.drondea.sms.message.smgp30.tlv;


import com.drondea.sms.message.smgp30.util.ByteUtil;

public class SmgpTLVString extends SmgpTLV {
    private String value;

    public SmgpTLVString() {
        super();
    }

    public SmgpTLVString(short tag) {
        super(tag);
    }

    public SmgpTLVString(short tag, int min, int max) {
        super(tag, min, max);
    }

    public SmgpTLVString(short tag, String value) throws Exception {
        super(tag);
        setValue(value);
    }

    public SmgpTLVString(short tag, int min, int max, String value) throws Exception {
        super(tag, min, max);
        setValue(value);
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        value = new String(ByteUtil.rtrimBytes(buffer));
        markValueSet();
    }

    @Override
    public byte[] getValueData() throws Exception {
        if (value == null) return null;
        byte[] valueBytes = value.getBytes();
        byte[] buffer = new byte[valueBytes.length + 1];
        System.arraycopy(valueBytes, 0, buffer, 0, valueBytes.length);
        buffer[valueBytes.length] = 0;
        return buffer;
    }

    public void setValue(String value) {
        this.value = value;
        if (value != null)
            markValueSet();
    }

    public String getValue() {
        return value;

    }

}