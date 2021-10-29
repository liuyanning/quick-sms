/**
 *
 */
package com.drondea.sms.common.util;

import java.nio.ByteBuffer;

/**
 *
 * @author huzorro(huzorro @ gmail.com)
 *
 */
public final class DefaultMsgIdUtil {

    public static byte[] msgId2Bytes(MsgId msgId) {
        byte[] bytes = new byte[8];

        long result = 0;
        if (msgId != null) {
            result |= (long) msgId.getMonth() << 60L;
            result |= (long) msgId.getDay() << 55L;
            result |= (long) msgId.getHour() << 50L;
            result |= (long) msgId.getMinutes() << 44L;
            result |= (long) msgId.getSeconds() << 38L;
            result |= (long) msgId.getGateId() << 16L;
            result |= (long) msgId.getSequenceId() & 0xffffL;
        }
        ByteBuffer.wrap(bytes).putLong(result);
        return bytes;

    }

    public static MsgId bytes2MsgId(byte[] bytes) {
        long result = ByteBuffer.wrap(bytes).getLong();
        int month = (int) ((result >>> 60) & 0xf);
        int day = (int) ((result >>> 55) & 0x1f);
        int hour = (int) ((result >>> 50) & 0x1f);
        int minutes = (int) ((result >>> 44) & 0x3f);
        int seconds = (int) ((result >>> 38) & 0x3f);
        int gateId = (int) ((result >>> 16) & 0x3fffff);
        int sequenceId = (int) (result & 0xffff);
        return new MsgId(month, day, hour, minutes, seconds, gateId, sequenceId);
    }
}
