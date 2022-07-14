package com.drondea.sms.message.sgip12;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.SgipConstants;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version V3.0.0
 * @description: 提交短信包
 * @author: liyuehai
 * @date: 2020年07月07日10:25
 **/
public class SgipSubmitRequestMessage extends AbstractSgipMessage implements ILongSMSMessage<SgipSubmitRequestMessage> {

    private String spNumber = GlobalConstants.EMPTY_STRING;
    private String chargeNumber = GlobalConstants.EMPTY_STRING;
    private short userCount = 1;
    private String[] userNumber = null;
    private String corpId = GlobalConstants.EMPTY_STRING;
    private String serviceType = GlobalConstants.EMPTY_STRING;
    private short feeType = 2;
    private String feeValue = GlobalConstants.EMPTY_STRING;
    private String givenValue = GlobalConstants.EMPTY_STRING;
    private short agentFlag = 0;
    private short morelatetomtFlag = 0;
    private short priority = 0;
    private String expireTime = GlobalConstants.EMPTY_STRING;
    private String scheduleTime = GlobalConstants.EMPTY_STRING;
    private short reportFlag = 1;
    private short tpPid = 0;
    private short tpUdhi = 0;
    private SmsDcs messageCoding = SgipConstants.DEFAULT_MSG_FMT;
    private short messageType = 0;
    private int messageLength = 120;
    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE;
    private String reserve = GlobalConstants.EMPTY_STRING;

    private SmsMessage msg;
    private String signature;

    private short pkTotal = 1;
    private short pkNumber = 1;
    private String batchNumber;
    private boolean isFixedSignature;

    public SgipSubmitRequestMessage() {
        super(SgipPackageType.SUBMITREQUEST);
    }

    public SgipSubmitRequestMessage(SgipHeader header) {
        super(SgipPackageType.SUBMITREQUEST, header);
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public String getChargeNumber() {
        return chargeNumber;
    }

    public void setChargeNumber(String chargeNumber) {
        this.chargeNumber = chargeNumber;
    }

    public short getUserCount() {
        return userCount;
    }

    public void setUserCount(short userCount) {
        this.userCount = userCount;
    }

    public String[] getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String[] userNumber) {
        this.userNumber = userNumber;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public short getFeeType() {
        return feeType;
    }

    public void setFeeType(short feeType) {
        this.feeType = feeType;
    }

    public String getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(String feeValue) {
        this.feeValue = feeValue;
    }

    public String getGivenValue() {
        return givenValue;
    }

    public void setGivenValue(String givenValue) {
        this.givenValue = givenValue;
    }

    public short getAgentFlag() {
        return agentFlag;
    }

    public void setAgentFlag(short agentFlag) {
        this.agentFlag = agentFlag;
    }

    public short getMorelatetomtFlag() {
        return morelatetomtFlag;
    }

    public void setMorelatetomtFlag(short morelatetomtFlag) {
        this.morelatetomtFlag = morelatetomtFlag;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public short getReportFlag() {
        return reportFlag;
    }

    public void setReportFlag(short reportFlag) {
        this.reportFlag = reportFlag;
    }

    public short getTpPid() {
        return tpPid;
    }

    public void setTpPid(short tpPid) {
        this.tpPid = tpPid;
    }

    public short getTpUdhi() {
        return tpUdhi;
    }

    public void setTpUdhi(short tpUdhi) {
        this.tpUdhi = tpUdhi;
    }

    public SmsDcs getMessageCoding() {
        return messageCoding;
    }

    public void setMessageCoding(SmsDcs messageCoding) {
        this.messageCoding = messageCoding;
    }

    public short getMessageType() {
        return messageType;
    }

    public void setMessageType(short messageType) {
        this.messageType = messageType;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public byte[] getMsgContentBytes() {
        return msgContentBytes;
    }

    public void setMsgContentBytes(byte[] msgContentBytes) {
        this.msgContentBytes = msgContentBytes;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    //计算BodyLength
    @Override
    public int getBodyLength() {
        return 123 + 21 * userCount + getMessageLength();
    }

    @Override
    public boolean isWindowSendMessage() {
        return true;
    }

    /**
     * 是否是长短信要看tpUdhi
     *
     * @return
     */
    @Override
    public boolean isLongMsg() {
        return this.tpUdhi == 1;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
    }

    /**
     * 设置文本短信内容用于发送
     */
    public void setMsgContent(String msgContent) {
        SmsTextMessage smsTextMessage = CommonUtil.buildTextMessage(msgContent);
        setMessageCoding((SmsDcs) smsTextMessage.getDcs());
        setMsg(smsTextMessage);
    }

    /**
     * 设置文本短信内容用于发送,同时设置内容编码格式
     */
    public void setMsgContent(String msgContent, SmsDcs msgFmt) {
        setMessageCoding(msgFmt);
        setMsg(CommonUtil.buildTextMessage(msgContent, msgFmt));
    }

    /**
     * 生成短信片段
     *
     * @return
     */
    @Override
    public LongMessageSlice generateSlice() {
        LongMessageSlice slice = new LongMessageSlice();
        slice.setTpPid(getTpPid());
        slice.setTpudhi(getTpUdhi());
        slice.setMsgFmt(getMessageCoding());
        slice.setMsgContentBytes(getMsgContentBytes());
        slice.setMsgLength((short) getMessageLength());
        slice.setSequence(getHeader().getSequenceId());
        return slice;
    }

    @Override
    public SgipSubmitRequestMessage generateMessage(LongMessageSlice frame, int sequenceNumber) throws Exception {
        SgipSubmitRequestMessage requestMessage = (SgipSubmitRequestMessage) this.clone();
        requestMessage.setPkNumber(frame.getPkNumber());
        requestMessage.setPkTotal(frame.getPkTotal());
        requestMessage.setTpUdhi(frame.getTpUdhi());
        requestMessage.setMessageCoding((SmsDcs) frame.getMsgFmt());
        requestMessage.setMsgContentBytes(frame.getMsgContentBytes());
        requestMessage.setMessageLength(frame.getMsgLength());
        long nodeId = requestMessage.getHeader().getSequenceNodeId();
        SgipSequenceNumber sgipSequenceNumber = new SgipSequenceNumber(nodeId, sequenceNumber);
        requestMessage.getHeader().setSequenceNumber(sgipSequenceNumber);
        requestMessage.setMsg(null);
        return requestMessage;
    }

    public void setMsg(SmsMessage msg) {
        this.msg = msg;
    }

    @Override
    public SmsMessage getSmsMessage() {
        return msg;
    }

    @Override
    public boolean isReport() {
        return false;
    }

    /**
     * 保存了所有的长短信的片段信息
     */
    private List<SgipSubmitRequestMessage> fragments = null;

    @Override
    public List<SgipSubmitRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SgipSubmitRequestMessage fragment) {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }

        fragments.add(fragment);
    }

    @Override
    public void setSmsMsg(SmsMessage smsMsg) {
        this.msg = smsMsg;
    }

    @Override
    public String getMsgContent() {
        if (this.msg instanceof SmsMessage) {
            return this.msg.toString();
        }

        if (this.msgContentBytes != null && this.msgContentBytes.length > 0) {
            LongMessageSlice slice = generateSlice();
            return LongMessageSliceManager.getPartTextMsg(slice);
        }

        return "";
    }

    @Override
    public int getSequenceNum() {
        return super.getSequenceId();
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String getMsgSignature() {
        return signature;
    }

    @Override
    public short getPkTotal() {
        return pkTotal;
    }

    @Override
    public void setPkTotal(short pkTotal) {
        this.pkTotal = pkTotal;
    }

    @Override
    public short getPkNumber() {
        return pkNumber;
    }

    @Override
    public void setPkNumber(short pkNumber) {
        this.pkNumber = pkNumber;
    }

    @Override
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public String getBatchNumber() {
        return this.batchNumber;
    }

    @Override
    public boolean isFixedSignature() {
        return isFixedSignature;
    }

    public void setFixedSignature(boolean fixedSignature) {
        isFixedSignature = fixedSignature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SgipSubmitRequestMessage [corpId=").append(corpId)
                .append(", spNumber=").append(spNumber)
                .append(", chargeNumber=").append(chargeNumber)
                .append(", corpId=").append(corpId)
                .append(", serviceType=").append(serviceType)
                .append(", feeType=").append(feeType)
                .append(", feeValue=").append(feeValue)
                .append(", userNumber=").append(Arrays.toString(userNumber))
                .append(", MessageContent=").append(getMsgContent())
                .append(", Header=").append(getHeader().toString()).append("]");
        return sb.toString();
    }
}
