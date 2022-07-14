/**
 *
 */
package com.drondea.sms.message.cmpp;


import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.common.util.SystemClock;
import com.drondea.sms.type.GlobalConstants;

import java.util.Calendar;

/**
 * 状态报告子包
 * @author liuyanning
 */
public class CmppReportRequestMessage {

    private MsgId msgId = new MsgId();
    private String stat = GlobalConstants.EMPTY_STRING;
    private String submitTime = "2101010101";
    private String doneTime = "2101010101";
    private String destterminalId = GlobalConstants.EMPTY_STRING;
    private long smscSequence = 0;

    /**
     * @return the msgId
     */
    public MsgId getMsgId() {
        return msgId;
    }

    /**
     * @param msgId
     *            the msgId to set
     */
    public void setMsgId(MsgId msgId) {
        this.msgId = msgId;
    }

    /**
     * @return the stat
     */
    public String getStat() {
        return stat;
    }

    /**
     * @param stat
     *            the stat to set
     */
    public void setStat(String stat) {
        this.stat = stat;
    }

    /**
     * @return the submitTime
     */
    public String getSubmitTime() {
        return submitTime;
    }

    /**
     * @param submitTime
     *            the submitTime to set
     */
    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    /**
     * @return the doneTime
     */
    public String getDoneTime() {
        return doneTime;
    }

    /**
     * @param doneTime
     *            the doneTime to set
     */
    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    /**
     * @return the destterminalId
     */
    public String getDestterminalId() {
        return destterminalId;
    }

    /**
     * @param destterminalId
     *            the destterminalId to set
     */
    public void setDestterminalId(String destterminalId) {
        this.destterminalId = destterminalId;
    }

    /**
     * @return the smscSequence
     */
    public long getSmscSequence() {
        return smscSequence;
    }

    /**
     * @param smscSequence
     *            the smscSequence to set
     */
    public void setSmscSequence(long smscSequence) {
        this.smscSequence = smscSequence;
    }

    public int getBodyLength() {
        return 71;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[msgId=").append(msgId).append(", stat=").append(stat).append(", submitTime=").append(submitTime)
                .append(", doneTime=").append(doneTime)
                .append(", destterminalId=").append(destterminalId).append(", smscSequence=").append(smscSequence).append("]");
        return sb.toString();
    }

}
