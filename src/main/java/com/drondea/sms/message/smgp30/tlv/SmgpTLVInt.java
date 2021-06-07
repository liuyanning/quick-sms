package com.drondea.sms.message.smgp30.tlv;

import com.drondea.sms.message.smgp30.util.ByteUtil;

public class SmgpTLVInt extends SmgpTLV {
    private int value = 0;

    public SmgpTLVInt() {
        super(4, 4);
    }

    public SmgpTLVInt(short p_tag) {
        super(p_tag, 4, 4);
    }

    public SmgpTLVInt(short p_tag, int p_value) {
        super(p_tag, 4, 4);
        value = p_value;
        markValueSet();
    }

    public void setValue(int p_value) {
        value = p_value;
        markValueSet();
    }

    public int getValue() {
        return value;

    }

    @Override
    public byte[] getValueData() throws Exception {

        return ByteUtil.int2byte(value);
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        value = ByteUtil.byte2int(buffer);
        markValueSet();
    }

}