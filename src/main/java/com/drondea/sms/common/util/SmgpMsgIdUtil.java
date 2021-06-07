/**
 *
 */
package com.drondea.sms.common.util;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SmgpMsgIdUtil {
    private static final Logger logger = LoggerFactory.getLogger(SmgpMsgIdUtil.class);

    public static byte[] msgId2Bytes(SmgpMsgId msgId) {

        byte[] bytes;
        try {
            bytes = Hex.decodeHex(msgId.toString().toCharArray());
        } catch (DecoderException e) {
            bytes = new byte[10];
        }
        return bytes;

    }


    public static SmgpMsgId bytes2SmgpMsgId(byte[] bytes) {

        assert (bytes.length == 10);
        String str = String.valueOf(Hex.encodeHex(bytes));
        try {
            return new SmgpMsgId(str);
        } catch (Exception ex) {
            logger.debug("Err MsgID : 0x{}", str);
            return new SmgpMsgId(bytes);
        }
    }
}
