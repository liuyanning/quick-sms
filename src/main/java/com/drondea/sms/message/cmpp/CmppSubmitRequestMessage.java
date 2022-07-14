package com.drondea.sms.message.cmpp;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.cmpp30.CmppPackageType;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version V3.0.0
 * @description: 提交短信包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:49
 **/
public class CmppSubmitRequestMessage extends AbstractCmppMessage implements ILongSMSMessage<CmppSubmitRequestMessage> {

    private MsgId msgId;
    private short pkTotal = 1;
    private short pkNumber = 1;
    private short registeredDelivery = 0;
    private short msgLevel = 9;
    private String serviceId = GlobalConstants.EMPTY_STRING;
    private short feeUserType = 2;
    private String feeTerminalId = GlobalConstants.EMPTY_STRING;
    private short feeTerminalType = 0;

    /**
     * 0是普通GSM 类型，点到点方式 ,127 :写sim卡
     */
    private short tpPid = 0;
    /**
     * 0:msgcontent不带协议头。1:带有协议头
     */
    private short tpUdhi = 0;
    private SmsDcs msgFmt = CmppConstants.DEFAULT_MSG_FMT;

    private String msgSrc = GlobalConstants.EMPTY_STRING;
    private String feeType = "01";
    private String feeCode = "000000";

    private String valIdTime = GlobalConstants.EMPTY_STRING;
    private String atTime = GlobalConstants.EMPTY_STRING;
    private String srcId = GlobalConstants.EMPTY_STRING;
    private short destUsrTl;
    private String[] destTerminalId = GlobalConstants.EMPTY_STRING_ARRAY;
    /**
     * cmpp3.0专有属性
     */
    private short destTerminalType = 0;

    private short msgLength = 140;
    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE;

    /**
     * cmpp3.0属性
     */
    private String linkId = GlobalConstants.EMPTY_STRING;

    /**
     * cmpp2.0属性
     */
    private String reserve = GlobalConstants.EMPTY_STRING;

    private SmsMessage msg;
    private String signature;
    private String batchNumber;
    private boolean isFixedSignature;

    public CmppSubmitRequestMessage() {
        super(CmppPackageType.SUBMITREQUEST);
    }

    public CmppSubmitRequestMessage(CmppHeader header) {
        super(CmppPackageType.SUBMITREQUEST, header);
    }

    //固定长度是151，加上32 * 目的电话数量，加上信息内容长度
    @Override
    public int getBodyLength30() {
        return 151 + 32 * getDestUsrTl() + getMsgLength();
    }

    @Override
    public int getBodyLength20() {
        return 126 + 21 * getDestUsrTl() + getMsgLength();
    }

    @Override
    public boolean isWindowSendMessage() {
        return true;
    }

    public MsgId getMsgId() {
        return msgId;
    }

    public void setMsgId(MsgId msgId) {
        this.msgId = msgId;
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
    public boolean isFixedSignature() {
        return isFixedSignature;
    }

    @Override
    public void setPkNumber(short pkNumber) {
        this.pkNumber = pkNumber;
    }

    public short getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public short getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(short msgLevel) {
        this.msgLevel = msgLevel;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public short getFeeUserType() {
        return feeUserType;
    }

    public void setFeeUserType(short feeUserType) {
        this.feeUserType = feeUserType;
    }

    public String getFeeTerminalId() {
        return feeTerminalId;
    }

    public void setFeeTerminalId(String feeTerminalId) {
        this.feeTerminalId = feeTerminalId;
    }

    public short getFeeTerminalType() {
        return feeTerminalType;
    }

    public void setFeeTerminalType(short feeTerminalType) {
        this.feeTerminalType = feeTerminalType;
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

    public SmsDcs getMsgFmt() {
        return msgFmt;
    }

    public void setMsgFmt(SmsDcs msgFmt) {
        this.msgFmt = msgFmt;
    }

    public String getMsgSrc() {
        return msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getValIdTime() {
        return valIdTime;
    }

    public void setValIdTime(String valIdTime) {
        this.valIdTime = valIdTime;
    }

    public String getAtTime() {
        return atTime;
    }

    public void setAtTime(String atTime) {
        this.atTime = atTime;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String[] getDestTerminalId() {
        return destTerminalId;
    }

    public void setDestTerminalId(String[] destTerminalId) {
        this.setDestUsrTl((short) destTerminalId.length);
        this.destTerminalId = destTerminalId;
    }

    public short getDestTerminalType() {
        return destTerminalType;
    }

    public void setDestTerminalType(short destTerminalType) {
        this.destTerminalType = destTerminalType;
    }

    public short getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    public byte[] getMsgContentBytes() {
        return msgContentBytes;
    }

    public void setMsgContentBytes(byte[] msgContentBytes) {
        this.msgContentBytes = msgContentBytes;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public short getDestUsrTl() {
        return destUsrTl;
    }

    public void setDestUsrTl(short destUsrTl) {
        this.destUsrTl = destUsrTl;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
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
        setMsgFmt((SmsDcs) smsTextMessage.getDcs());
        setMsg(smsTextMessage);
    }

    /**
     * 设置文本短信内容用于发送,同时设置内容编码格式
     */
    public void setMsgContent(String msgContent, SmsDcs msgFmt) {
        setMsgFmt(msgFmt);
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
        slice.setMsgFmt(getMsgFmt());
        slice.setMsgContentBytes(getMsgContentBytes());
        slice.setMsgLength(getMsgLength());
        slice.setSequence(getHeader().getSequenceId());
        return slice;
    }

    @Override
    public CmppSubmitRequestMessage generateMessage(LongMessageSlice frame, int sequenceId) throws Exception {
        CmppSubmitRequestMessage requestMessage = (CmppSubmitRequestMessage) this.clone();
        requestMessage.setPkNumber(frame.getPkNumber());
        requestMessage.setPkTotal(frame.getPkTotal());
        requestMessage.setTpUdhi(frame.getTpUdhi());
        requestMessage.setMsgFmt((SmsDcs) frame.getMsgFmt());
        requestMessage.setMsgContentBytes(frame.getMsgContentBytes());
        requestMessage.setMsgLength(frame.getMsgLength());
        requestMessage.getHeader().setSequenceId(sequenceId);
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
    private List<CmppSubmitRequestMessage> fragments = null;

    @Override
    public List<CmppSubmitRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(CmppSubmitRequestMessage fragment) {
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
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public String getBatchNumber() {
        return this.batchNumber;
    }

    public void setFixedSignature(boolean fixedSignature) {
        isFixedSignature = fixedSignature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CmppSubmitRequestMessage [msgId=").append(msgId).append(", serviceId=").append(serviceId).append(", srcId=").append(srcId)
                .append(", feeTerminalId=").append(feeTerminalId).append(", feeUserType=").append(feeUserType)
                .append(", msgSrc=").append(msgSrc).append(", destTerminalId=").append(Arrays.toString(destTerminalId)).append(", msgContent=")
                .append(getMsgContent()).append(", sequenceId=").append(getHeader().getSequenceId()).append(", msgFmt=").append(getMsgFmt().getValue()).append("]");
        return sb.toString();
    }
}
