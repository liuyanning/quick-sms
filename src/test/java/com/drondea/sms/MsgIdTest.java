package com.drondea.sms;

import com.drondea.sms.common.util.DefaultMsgIdUtil;
import com.drondea.sms.common.util.MsgId;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * @version V3.0.0
 * @description: test
 * @author: 刘彦宁
 * @date: 2020年07月31日14:04
 **/
public class MsgIdTest {

    public static void main(String[] args) {
        MsgId msgid = new MsgId();
        System.out.println(msgid.toString());

        byte[] bytes = DefaultMsgIdUtil.msgId2Bytes(msgid);
        System.out.println(Hex.encodeHex(bytes, true));

        String hex = new String(Hex.encodeHex(bytes, true));
        System.out.println(hex);
        try {
            MsgId msgId1 = DefaultMsgIdUtil.bytes2MsgId(Hex.decodeHex(hex));
            System.out.println(msgId1.toString());
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        System.out.println("0804160654006742800001".getBytes().length + 1);

    }
}
