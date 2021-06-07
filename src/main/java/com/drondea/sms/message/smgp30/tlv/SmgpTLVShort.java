package com.drondea.sms.message.smgp30.tlv;


import com.drondea.sms.message.smgp30.util.ByteUtil;

public class SmgpTLVShort extends SmgpTLV {
    private short value = 0;

    public SmgpTLVShort() {
        super(2, 2);
    }

    public SmgpTLVShort(short p_tag) {
        super(p_tag, 2, 2);
    }

    public SmgpTLVShort(short p_tag, short p_value) {
        super(p_tag, 2, 2);
        value = p_value;
        markValueSet();
    }

    public void setValue(short p_value) {
        value = p_value;
        markValueSet();
    }

    public short getValue() {
        return value;
    }

    @Override
    public byte[] getValueData() throws Exception {
        return ByteUtil.short2byte(value);
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        value = ByteUtil.byte2short(buffer);
        markValueSet();
    }

}