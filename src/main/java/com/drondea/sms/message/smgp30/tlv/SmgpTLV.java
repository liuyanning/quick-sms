package com.drondea.sms.message.smgp30.tlv;


import com.drondea.sms.message.smgp30.util.ByteUtil;

import java.io.Serializable;


public abstract class SmgpTLV implements Serializable, Cloneable {
    /**
     * The tag of the instance of SmgpTLV as defined in SMPP v3.4
     */
    private short tag = 0;

    private boolean valueIsSet = false; // must be set by setValueData()

    private static int DONT_CHECK_LIMIT = -1;

    private int minLength = DONT_CHECK_LIMIT;

    private int maxLength = DONT_CHECK_LIMIT;

    public SmgpTLV() {
        super();
    }

    public SmgpTLV(short tag) {
        super();
        this.tag = tag;
    }

    public SmgpTLV(int min, int max) {
        super();
        minLength = min;
        maxLength = max;
    }

    /**
     * Sets all the necessary params of the SmgpTLV.
     */
    public SmgpTLV(short tag, int min, int max) {
        super();
        this.tag = tag;
        minLength = min;
        maxLength = max;
    }

    public abstract void setValueData(byte[] buffer) throws Exception;

    public abstract byte[] getValueData() throws Exception;

    /**
     * Sets the tag of this SmgpTLV.
     */
    public void setTag(short tag) {
        this.tag = tag;
    }

    /**
     * Returns the tag of this SmgpTLV.
     */
    public short getTag() {
        return tag;
    }

    public short getLength() {
        try {
            if (hasValue()) {
                byte[] valueBuf = getValueData();
                if (valueBuf != null) {
                    int length = valueBuf.length;
                    return (short) length;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public void fromBytes(byte[] buffer) throws Exception {
        int index = 0;
        short newTag = ByteUtil.byte2short(buffer, index);
        index += 2;
        short length = ByteUtil.byte2short(buffer, index);
        index += 2;
        byte[] valueBytes = new byte[length];
        System.arraycopy(buffer, index, valueBytes, 0, length);
        setValueData(valueBytes);
        setTag(newTag);
    }

    public int fromBytes(byte[] buffer, int offset) throws Exception {

        short newTag = ByteUtil.byte2short(buffer, offset);
        offset += 2;
        short length = ByteUtil.byte2short(buffer, offset);
        offset += 2;
        byte[] valueBytes = new byte[length];
        System.arraycopy(buffer, offset, valueBytes, 0, length);
        setValueData(valueBytes);
        setTag(newTag);
        return offset;
    }

    public byte[] toBytes() throws Exception {
        if (hasValue()) {
            int length = getLength();
            byte[] bytes = new byte[4 + length];
            int index = 0;
            ByteUtil.short2byte(getTag(), bytes, index);
            index += 2;
            ByteUtil.short2byte(getLength(), bytes, index);
            index += 2;
            byte[] valueBytes = getValueData();
            System.arraycopy(valueBytes, 0, bytes, index, length);

            return bytes;
        } else {
            return null;
        }
    }

    public int toBytes(byte[] buffer, int offset) throws Exception {
        if (hasValue()) {
            int length = getLength();

            ByteUtil.short2byte(getTag(), buffer, offset);
            offset += 2;
            ByteUtil.short2byte(getLength(), buffer, offset);
            offset += 2;
            byte[] valueBytes = getValueData();
            System.arraycopy(valueBytes, 0, buffer, offset, length);
            offset += length;

        }
        return offset;
    }

    protected void markValueSet() {
        valueIsSet = true;
    }

    public boolean hasValue() {
        return valueIsSet;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof SmgpTLV)) {
            return getTag() == ((SmgpTLV) obj).getTag();
        }
        return false;
    }

    @Override
    public SmgpTLV clone() throws CloneNotSupportedException {
        return (SmgpTLV) super.clone();
    }
}
