package com.drondea.sms.message.smgp30.msg;


import com.drondea.sms.common.util.CommonUtil;
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

import java.util.ArrayList;
import java.util.List;

public class SmgpSubmitRequestMessage extends AbstractSmgpMessage implements ILongSMSMessage<SmgpSubmitRequestMessage> {

    private short msgType = (short) 6; // 1

    private boolean needReport = true; // 1

    private short priority = 2; // 1

    private String serviceId = ""; // 10

    private String feeType = "00"; // 2

    private String feeCode = "000000"; // 6

    private String fixedFee = "000000"; // 6  v3.0新增字段

    private SmsDcs msgFmt = SmgpConstants.DEFAULT_MSG_FMT;

    private String validTime = ""; // 17

    private String atTime = ""; // 17

    private String srcTermId = ""; // 21

    private String chargeTermId = ""; // 21

    private short destTermIdCount; // 1

    private String[] destTermIdArray = GlobalConstants.EMPTY_STRING_ARRAY; // 21*destTermIdCount

    private short msgLength = 140;//1

    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE; // msgLength

    private String reserve = ""; // 8

    private SmsMessage msg;
    private String signature;
    private String batchNumber;

    public SmgpSubmitRequestMessage(SmgpHeader header) {
        super(SmgpPackageType.SUBMITREQUEST, header);
        registerOptional(tpPid);
        registerOptional(tpUdhi);
        registerOptional(linkId);
        registerOptional(msgSrc);
        registerOptional(chargeUserType);
        registerOptional(chargeTermType);
        registerOptional(chargeTermPseudo);
        registerOptional(destTermType);
        registerOptional(destTermPseudo);
        registerOptional(pkTotal);
        registerOptional(pkNumber);
        registerOptional(submitMsgType);
        registerOptional(spDealResult);
        registerOptional(mServiceId);
    }

    public SmgpSubmitRequestMessage() {
        super(SmgpPackageType.SUBMITREQUEST);
        registerOptional(tpPid);
        registerOptional(tpUdhi);
        registerOptional(linkId);
        registerOptional(msgSrc);
        registerOptional(chargeUserType);
        registerOptional(chargeTermType);
        registerOptional(chargeTermPseudo);
        registerOptional(destTermType);
        registerOptional(destTermPseudo);
        registerOptional(pkTotal);
        registerOptional(pkNumber);
        registerOptional(submitMsgType);
        registerOptional(spDealResult);
        registerOptional(mServiceId);
    }

    private SmgpTLVByte tpPid = new SmgpTLVByte(SmgpConstants.OPT_TP_PID);
    private SmgpTLVByte tpUdhi = new SmgpTLVByte(SmgpConstants.OPT_TP_UDHI);
    private SmgpTLVString linkId = new SmgpTLVString(SmgpConstants.OPT_LINK_ID);
    private SmgpTLVString msgSrc = new SmgpTLVString(SmgpConstants.OPT_MSG_SRC);
    private SmgpTLVByte chargeUserType = new SmgpTLVByte(SmgpConstants.OPT_CHARGE_USER_TYPE);
    private SmgpTLVByte chargeTermType = new SmgpTLVByte(SmgpConstants.OPT_CHARGE_TERM_TYPE);
    private SmgpTLVString chargeTermPseudo = new SmgpTLVString(SmgpConstants.OPT_CHARGE_TERM_PSEUDO);
    private SmgpTLVByte destTermType = new SmgpTLVByte(SmgpConstants.OPT_DEST_TERM_TYPE);
    private SmgpTLVString destTermPseudo = new SmgpTLVString(SmgpConstants.OPT_DEST_TERM_PSEUDO);
    private SmgpTLVByte pkTotal = new SmgpTLVByte(SmgpConstants.OPT_PK_TOTAL);
    private SmgpTLVByte pkNumber = new SmgpTLVByte(SmgpConstants.OPT_PK_NUMBER);
    private SmgpTLVByte submitMsgType = new SmgpTLVByte(SmgpConstants.OPT_SUBMIT_MSG_TYPE);
    private SmgpTLVByte spDealResult = new SmgpTLVByte(SmgpConstants.OPT_SP_DEAL_RESULT);
    private SmgpTLVString mServiceId = new SmgpTLVString(SmgpConstants.OPT_M_SERVICE_ID);

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

    public void setMsgSrc(String value) {
        msgSrc.setValue(value);
    }

    public String getMsgSrc() {
        return msgSrc.getValue();
    }

    public void setChargeUserType(byte value) {
        chargeUserType.setValue(value);
    }

    public byte getChargeUserType() {
        return chargeUserType.getValue();
    }

    public void setChargeTermType(byte value) {
        chargeTermType.setValue(value);
    }

    public byte getChargeTermType() {
        return chargeTermType.getValue();
    }

    public void setChargeTermPseudo(String value) {
        chargeTermPseudo.setValue(value);
    }

    public String getChargeTermPseudo() {
        return chargeTermPseudo.getValue();
    }

    @Override
    public boolean isReport() {
        return false;
    }

    public void setDestTermType(byte value) {
        destTermType.setValue(value);
    }

    public byte getDestTermType() {
        return destTermType.getValue();
    }

    public void setDestTermPseudo(String value) {
        destTermPseudo.setValue(value);
    }

    public String getDestTermPseudo() {
        return destTermPseudo.getValue();
    }


    @Override
    public void setPkTotal(short value) {
        pkTotal.setValue((byte) value);
    }

    @Override
    public short getPkTotal() {
        return pkTotal.getValue();
    }

    @Override
    public void setPkNumber(short value) {
        pkNumber.setValue((byte) value);
    }

    @Override
    public short getPkNumber() {
        return pkNumber.getValue();
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

    public void setMServiceId(String value) {
        mServiceId.setValue(value);
    }

    public String getMServiceId() {
        return mServiceId.getValue();
    }

    public void setDestTermIdCount(short destTermIdCount) {
        this.destTermIdCount = destTermIdCount;
    }

    public short getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    public short getMsgType() {
        return msgType;
    }

    public void setMsgType(short msgType) {
        this.msgType = msgType;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public boolean getNeedReport() {
        return needReport;
    }

    public void setNeedReport(boolean needReport) {
        this.needReport = needReport;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getFeeType() {
        return this.feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeCode() {
        return this.feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getFixedFee() {
        return this.fixedFee;
    }

    public void setFixedFee(String fixedFee) {
        this.fixedFee = fixedFee;
    }

    public SmsDcs getMsgFmt() {
        return this.msgFmt;
    }

    public void setMsgFmt(SmsDcs msgFmt) {
        this.msgFmt = msgFmt;
    }

    public String getValidTime() {
        return this.validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getAtTime() {
        return this.atTime;
    }

    public void setAtTime(String atTime) {
        this.atTime = atTime;
    }

    public String getSrcTermId() {
        return this.srcTermId;
    }

    public void setSrcTermId(String srcTermId) {
        this.srcTermId = srcTermId;
    }

    public String getChargeTermId() {
        return this.chargeTermId;
    }

    public void setChargeTermId(String chargeTermId) {
        this.chargeTermId = chargeTermId;
    }

    public short getDestTermIdCount() {
        return this.destTermIdCount;
    }

    public String[] getDestTermIdArray() {
        return destTermIdArray;
    }

    public void setDestTermIdArray(String destTermIdArray) {
        this.destTermIdArray = new String[]{destTermIdArray};
        this.destTermIdCount = (short) 1;
    }

    public void setDestTermIdArray(String[] destTermIdArray) {
        this.destTermIdArray = destTermIdArray;
        this.destTermIdCount = (short) (destTermIdArray == null ? 0 : destTermIdArray.length);
    }

    public void setMsgContent(String msgContent) {
        SmsTextMessage smsTextMessage = CommonUtil.buildTextMessage(msgContent);
        setMsgFmt((SmsDcs) smsTextMessage.getDcs());
        setMsgContent(smsTextMessage);
    }

    /**
     * 设置文本短信内容用于发送,同时设置内容编码格式
     */
    public void setMsgContent(String msgContent, SmsDcs msgFmt) {
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

    public byte[] getMsgContentBytes() {
        return msgContentBytes;
    }

    public void setMsgContentBytes(byte[] msgContentBytes) {
        this.msgContentBytes = msgContentBytes;
    }

    public String getReserve() {
        return this.reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    @Override
    public String getMsgContent() {
        if (msg instanceof SmsMessage) {
            return msg.toString();
        }

        if (msgContentBytes != null && msgContentBytes.length > 0) {
            LongMessageSlice slice = generateSlice();
            return LongMessageSliceManager.getPartTextMsg(slice);
        }

        return "";
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpSubmitRequestMessage:[SequenceId=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("CommandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("msgType=").append(msgType).append(",");
        buffer.append("needReport=").append(needReport).append(",");
        buffer.append("validTime=").append(validTime).append(",");
        buffer.append("atTime=").append(atTime).append(",");
        buffer.append("srcTermId=").append(srcTermId).append(",");
        buffer.append("chargeTermId=").append(chargeTermId).append(",");
        buffer.append("destTermIdArray={");
        for (int i = 0; i < destTermIdCount; i++) {
            if (i == 0) {
                buffer.append(destTermIdArray[i]);
            } else {
                buffer.append(";" + destTermIdArray[i]);
            }
        }
        buffer.append("},");
        buffer.append("msgContent=").append(getMsgContent()).append("]");
        buffer.append("reserve=").append(getReserve()).append("]");
        return buffer.toString();
    }

    @Override
    public int getBodyLength() {
        int length = 114 + 21 * getDestTermIdCount() + getMsgLength();
        if (0x13 == SmgpConstants.DEFAULT_VERSION) {//0x13 版本 没有 FixedFee 6字节
            length = length - 6;
        }
        return length + getTLVLength();
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
        frame.setMsgContentBytes(getMsgContentBytes());
        frame.setMsgLength((short) msgContentBytes.length);
        frame.setSequence(getSequenceId());
        return frame;
    }

    @Override
    public SmgpSubmitRequestMessage generateMessage(LongMessageSlice frame, int sequenceId) throws Exception {
        SmgpSubmitRequestMessage requestMessage = (SmgpSubmitRequestMessage) this.clone();
        requestMessage.setTpUdhi((byte) frame.getTpUdhi());
        requestMessage.setMsgFmt((SmsDcs) frame.getMsgFmt());
        requestMessage.setMsgLength(frame.getMsgLength());
        requestMessage.setMsgContentBytes(frame.getMsgContentBytes());
        requestMessage.setPkTotal((byte) frame.getPkTotal());
        //重新生成pkNumber对象
        SmgpTLVByte pkNumber = new SmgpTLVByte(SmgpConstants.OPT_PK_NUMBER);
        pkNumber.setValue((byte) frame.getPkNumber());
        requestMessage.replaceTLVByTag(pkNumber);
        requestMessage.pkNumber = pkNumber;
        if (frame.getPkNumber() != 1) {
            requestMessage.getHeader().setSequenceId(sequenceId);
        }
        requestMessage.setMsgContent((SmsMessage) null);
        return requestMessage;
    }



    private List<SmgpSubmitRequestMessage> fragments = null;

    @Override
    public List<SmgpSubmitRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SmgpSubmitRequestMessage fragment) {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        fragments.add(fragment);
    }

    @Override
    public void setSmsMsg(SmsMessage smsMsg) {
        this.msg = smsMsg;
    }

    /**
     * 是否是长短信要看tpUdhi
     *
     * @return
     */
    @Override
    public boolean isLongMsg() {
        return this.tpUdhi.getValue() == 1;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
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
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public String getBatchNumber() {
        return this.batchNumber;
    }


}
