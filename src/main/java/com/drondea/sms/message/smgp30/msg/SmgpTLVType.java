package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.message.smgp30.tlv.SmgpTLV;
import com.drondea.sms.message.smgp30.tlv.SmgpTLVByte;
import com.drondea.sms.message.smgp30.tlv.SmgpTLVString;
import com.drondea.sms.type.SmgpConstants;

/**
 * @version V3.0
 * @description: smgp的tlv tag对应参数
 * @author: ywj
 * @date: 2020年06月08日10:58
 **/
public enum SmgpTLVType {
    /**
     * GSM协议类型 1
     */
    TP_PID(SmgpConstants.OPT_TP_PID, new SmgpTLVByte(SmgpConstants.OPT_TP_PID)),
    /**
     * GSM协议类型 1
     */
    TP_UDHI(SmgpConstants.OPT_TP_UDHI, new SmgpTLVByte(SmgpConstants.OPT_TP_UDHI)),
    /**
     * 交易标识 20
     */
    LINK_ID(SmgpConstants.OPT_LINK_ID, new SmgpTLVString(SmgpConstants.OPT_LINK_ID)),
    /**
     * 信息内容的来源 8
     */
    MSG_SRC(SmgpConstants.OPT_MSG_SRC, new SmgpTLVString(SmgpConstants.OPT_MSG_SRC)),
    /**
     * 计费用户类型 1
     */
    CHARGE_USER_TYPE(SmgpConstants.OPT_CHARGE_USER_TYPE, new SmgpTLVByte(SmgpConstants.OPT_CHARGE_USER_TYPE)),
    /**
     * 计费用户的号码类型 1
     */
    CHARGE_TERM_TYPE(SmgpConstants.OPT_CHARGE_TERM_TYPE, new SmgpTLVByte(SmgpConstants.OPT_CHARGE_TERM_TYPE)),
    /**
     * 计费用户的伪码 Length
     */
    CHARGE_TERM_PSEUDO(SmgpConstants.OPT_CHARGE_TERM_PSEUDO, new SmgpTLVString(SmgpConstants.OPT_CHARGE_TERM_PSEUDO)),
    /**
     * 短消息接收方号码的类型 1
     */
    DEST_TERM_TYPE(SmgpConstants.OPT_DEST_TERM_TYPE, new SmgpTLVByte(SmgpConstants.OPT_DEST_TERM_TYPE)),
    /**
     * 短消息接收方的伪码 Length
     */
    DEST_TERM_PSEUDO(SmgpConstants.OPT_DEST_TERM_PSEUDO, new SmgpTLVString(SmgpConstants.OPT_DEST_TERM_PSEUDO)),
    /**
     * 相同MsgID的消息总条数 1
     */
    PK_TOTAL(SmgpConstants.OPT_PK_TOTAL, new SmgpTLVByte(SmgpConstants.OPT_PK_TOTAL)),
    /**
     * 相同MsgID的消息序号 1
     */
    PK_NUMBER(SmgpConstants.OPT_PK_NUMBER, new SmgpTLVByte(SmgpConstants.OPT_PK_NUMBER)),
    /**
     * SP发送的消息类型 1
     */
    SUBMIT_MSG_TYPE(SmgpConstants.OPT_SUBMIT_MSG_TYPE, new SmgpTLVByte(SmgpConstants.OPT_SUBMIT_MSG_TYPE)),
    /**
     * SP对消息的处理结果 1
     */
    SP_DEAL_RESULT(SmgpConstants.OPT_SP_DEAL_RESULT, new SmgpTLVByte(SmgpConstants.OPT_SP_DEAL_RESULT)),
    /**
     * 业务代码（用于移动网业务）21
     */
    M_SERVICE_ID(SmgpConstants.OPT_M_SERVICE_ID, new SmgpTLVString(SmgpConstants.OPT_M_SERVICE_ID));

    private short tag;
    private SmgpTLV tlv;

    SmgpTLVType(short tag, SmgpTLV tlv) {
        this.tag = tag;
        this.tlv = tlv;
    }

    public short getTag() {
        return tag;
    }

    public SmgpTLV getSmgpTLV() {
        return tlv;
    }

    public static SmgpTLV getValveByKey(short tag) {
        SmgpTLVType[] values = SmgpTLVType.values();
        for (SmgpTLVType tlvType : values) {
            if (tlvType.getTag() == tag) {
                return tlvType.getSmgpTLV();
            }
        }
        return null;
    }

}
