package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SmgpMsgId;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.message.smgp30.tlv.SmgpTLVByte;
import com.drondea.sms.message.smgp30.tlv.SmgpTLVString;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.SmgpConstants;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmgpDeliverRequestMessage extends AbstractSmgpMessage implements ILongSMSMessage<SmgpDeliverRequestMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SmgpDeliverRequestMessage.class);
    /**
     *
     */
    private static final long serialVersionUID = -6960208317220566142L;

    private SmgpMsgId msgId; // 10

    private boolean isReport; // 1

    private SmsDcs msgFmt = SmgpConstants.DEFAULT_MSG_FMT;

    private String recvTime = DateFormatUtils.format((new Date()), "yyyyMMddHHmmss"); // 14

    private String srcTermId = GlobalConstants.EMPTY_STRING; // 21

    private String destTermId = GlobalConstants.EMPTY_STRING; // 21

    private short msgLength = 140;

    private byte[] bMsgContent = GlobalConstants.EMPTY_BYTE; // msgLength

    private String reserve = GlobalConstants.EMPTY_STRING; // 8

    private SmsMessage msg;

    private SmgpReportMessage report;

    private short pkTotal = 1;
    private short pkNumber = 1;

    private SmgpTLVByte tpPid = new SmgpTLVByte(SmgpConstants.OPT_TP_PID);
    private SmgpTLVByte tpUdhi = new SmgpTLVByte(SmgpConstants.OPT_TP_UDHI);
    private SmgpTLVString linkId = new SmgpTLVString(SmgpConstants.OPT_LINK_ID);
    private SmgpTLVByte srcTermType = new SmgpTLVByte(SmgpConstants.OPT_SRC_TERM_TYPE);
    private SmgpTLVString srcTermPseudo = new SmgpTLVString(SmgpConstants.OPT_SRC_TERM_PSEUDO);
    private SmgpTLVByte submitMsgType = new SmgpTLVByte(SmgpConstants.OPT_SUBMIT_MSG_TYPE);
    private SmgpTLVByte spDealResult = new SmgpTLVByte(SmgpConstants.OPT_SP_DEAL_RESULT);
    private String batchNumber;

    public SmgpDeliverRequestMessage() {
        super(SmgpPackageType.DELIVERREQUEST);
        registerOptional(tpPid);
        registerOptional(tpUdhi);
        registerOptional(linkId);
        registerOptional(srcTermType);
        registerOptional(srcTermPseudo);
        registerOptional(submitMsgType);
        registerOptional(spDealResult);
    }

    public SmgpDeliverRequestMessage(SmgpHeader header) {
        super(SmgpPackageType.DELIVERREQUEST, header);
        registerOptional(tpPid);
        registerOptional(tpUdhi);
        registerOptional(linkId);
        registerOptional(srcTermType);
        registerOptional(srcTermPseudo);
        registerOptional(submitMsgType);
        registerOptional(spDealResult);
    }

    public void setTpPid(byte value) {
        tpPid.setValue(value);
    }

    public byte getTpPid() {
        return tpPid.getValue();
    }

    public void setTpUdhi(byte value) {
        tpUdhi.setValue(value);
    }

    public byte getTpUdhi() {
        return tpUdhi.getValue();
    }

    public void setLinkId(String value) {
        linkId.setValue(value);
    }

    public String getLinkId() {
        return linkId.getValue();
    }

    public short getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    public void setSrcTermType(byte value) {
        srcTermType.setValue(value);
    }

    public byte getSrcTermType() {
        return srcTermType.getValue();
    }

    public void setSrcTermPseudo(String value) {
        srcTermPseudo.setValue(value);
    }

    public String getSrcTermPseudo() {
        return srcTermPseudo.getValue();
    }

    public void setSubmitMsgType(byte value) {
        submitMsgType.setValue(value);
    }

    public byte getSubmitMsgType() {
        return submitMsgType.getValue();
    }

    public void setSpDealResult(byte value) {
        spDealResult.setValue(value);
    }

    public byte getSpDealResult() {
        return spDealResult.getValue();
    }

    public SmgpMsgId getSmgpMsgId() {
        return this.msgId;
    }

    public void setSmgpMsgId(SmgpMsgId msgId) {
        this.msgId = msgId;
    }

    @Override
    public boolean isReport() {
        return isReport;
    }

    public boolean getIsReport() {
        return isReport();
    }

    public void setIsReport(boolean report) {
        isReport = report;
    }

    public SmsDcs getMsgFmt() {
        return this.msgFmt;
    }

    public void setMsgFmt(SmsDcs msgFmt) {
        this.msgFmt = msgFmt;
    }

    public String getRecvTime() {
        return this.recvTime;
    }

    public void setRecvTime(String recvTime) {
        this.recvTime = recvTime;
    }

    public void setRecvTime(Date recvTime) {
        this.recvTime = DateFormatUtils.format(recvTime, "yyyyMMddHHmmss");
    }

    public String getSrcTermId() {
        return this.srcTermId;
    }

    public void setSrcTermId(String srcTermId) {
        this.srcTermId = srcTermId;
    }

    public String getDestTermId() {
        return this.destTermId;
    }

    public void setDestTermId(String destTermId) {
        this.destTermId = destTermId;
    }

    public byte[] getBMsgContent() {
        return this.bMsgContent;
    }

    public void setBMsgContent(byte[] msgContent) {
        bMsgContent = msgContent;
    }

    public void setMsgContent(String msgContent) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        SmsTextMessage smsTextMessage = CommonUtil.buildTextMessage(msgContent);
        setMsgFmt((SmsDcs) smsTextMessage.getDcs());
        setMsgContent(smsTextMessage);
    }

    public void setMsgContent(String msgContent, SmsDcs msgFmt) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        setMsgFmt(msgFmt);
        setMsgContent(CommonUtil.buildTextMessage(msgContent, msgFmt));
    }

    public void setMsgContent(SmsMessage msg) {
        this.msg = msg;
    }

    @Override
    public SmsMessage getSmsMessage() {
        return msg;
    }

    public SmgpReportMessage getReport() {
        return report;
    }

    public void setReport(SmgpReportMessage report) {
        this.report = report;
        this.isReport = true;
    }

    public String getReserve() {
        return this.reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    private String msgIdString() {
        return msgId.toString();
    }

    @Override
    public String getMsgContent() {
        if (msg instanceof SmsMessage) {
            return msg.toString();
        }

        if (bMsgContent != null && bMsgContent.length > 0) {
            LongMessageSlice slice = generateSlice();
            return LongMessageSliceManager.getPartTextMsg(slice);
        }

        return "";
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpDeliverRequestMessage:[sequenceId=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("CommandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("msgId=").append(msgIdString()).append(",");
        buffer.append("recvTime=").append(recvTime).append(",");
        buffer.append("srcTermId=").append(srcTermId).append(",");
        buffer.append("destTermId=").append(destTermId).append(",");
        if (isReport) {
            buffer.append("ReportData=").append(getReport()).append("]");
            return buffer.toString();
        }
        buffer.append("msgContent=").append(getMsgContent()).append("]");

        return buffer.toString();
    }

    @Override
    public boolean isWindowSendMessage() {
        return true;
    }

    @Override
    public LongMessageSlice generateSlice() {
        LongMessageSlice frame = new LongMessageSlice();
        frame.setTpPid(getTpPid());
        frame.setTpudhi(getTpUdhi());
        frame.setMsgFmt(getMsgFmt());
        frame.setMsgContentBytes(getBMsgContent());
        frame.setMsgLength((short) this.bMsgContent.length);
        frame.setSequence(getSequenceId());
        return frame;
    }

    @Override
    public SmgpDeliverRequestMessage generateMessage(LongMessageSlice frame, int sequenceId) throws Exception {
        SmgpDeliverRequestMessage requestMessage = (SmgpDeliverRequestMessage) this.clone();

        requestMessage.setTpUdhi((byte) frame.getTpUdhi());
        requestMessage.setMsgFmt((SmsDcs) frame.getMsgFmt());
        requestMessage.setBMsgContent(frame.getMsgContentBytes());
        requestMessage.setMsgLength(frame.getMsgLength());
        if (frame.getPkNumber() != 1) {
            requestMessage.getHeader().setSequenceId(sequenceId);
        }
        requestMessage.setMsgContent((SmsMessage) null);
        return requestMessage;
    }

    private List<SmgpDeliverRequestMessage> fragments = null;

    @Override
    public List<SmgpDeliverRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SmgpDeliverRequestMessage fragment) {
        if (fragments == null) {
            fragments = new ArrayList<SmgpDeliverRequestMessage>();
        }

        fragments.add(fragment);
    }

    @Override
    public boolean isLongMsg() {
        return this.tpUdhi.getValue() == 1;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
    }

    @Override
    public void setSmsMsg(SmsMessage smsMsg) {
        this.msg = smsMsg;
    }

    @Override
    public int getSequenceNum() {
        return super.getSequenceId();
    }

    @Override
    public String getMsgSignature() {
        return null;
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
    public int getBodyLength() {
        int length = 77;
        if (getIsReport()) {//是report
            length += SmgpReportMessage.LENGTH;
        } else {
            length += getMsgLength();
        }
        return length + getTLVLength();
    }
}