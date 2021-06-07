package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.common.util.SmgpMsgId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * smgp report类 66字节
 */
public class SmgpReportMessage {
    private static final Logger logger = LoggerFactory.getLogger(SmgpReportMessage.class);
    public static final int LENGTH = 66 + "id: sub: dlvrd: submit date: done date: stat: err: text:".length();

    private SmgpMsgId msgId = new SmgpMsgId(); // 10
    private String sub = "001"; // 3
    private String dlvrd = "001"; // 3
    private String subTime = ""; // 10;
    private String doneTime = ""; // 10;
    private String stat = ""; // 7
    private String err = ""; // 3
    private String txt = ""; // 20

    public String getDlvrd() {
        return dlvrd;
    }

    public void setDlvrd(String dlvrd) {
        this.dlvrd = dlvrd;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    public String getErr() {
        return err == null ? "" : err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public SmgpMsgId getSmgpMsgId() {
        return msgId;
    }

    public void setSmgpMsgId(SmgpMsgId msgId) {
        this.msgId = msgId;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    private String msgIdString() {
        return msgId.toString();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{msgId=").append(msgIdString()).append(",").append("sub=").append(sub).append(",").append("dlvrd=").append(dlvrd).append(",")
                .append("subTime=").append(subTime).append(",").append("doneTime=").append(doneTime).append(",").append("stat=").append(stat).append(",")
                .append("err=").append(err).append(",").append("text=").append(txt).append("}");
        return buffer.toString();
    }
}
