package com.drondea.sms.message.cmpp;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.cmpp30.CmppPackageType;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V3.0.0
 * @description: 心跳包
 * @author: 刘彦宁
 * @date: 2020年06月08日10:49
 **/
public class CmppDeliverRequestMessage extends AbstractCmppMessage implements ILongSMSMessage<CmppDeliverRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(CmppDeliverRequestMessage.class);

    private MsgId msgId;
    private String destId = GlobalConstants.EMPTY_STRING;
    private String serviceid = GlobalConstants.EMPTY_STRING;
    /**
     * 0是普通GSM 类型，点到点方式 ,127 :写sim卡
     */
    private short tpPid = 0;
    /**
     * 0:msgcontent不带协议头。1:带有协议头
     */
    private short tpUdhi = 0;
    private SmsDcs msgFmt = CmppConstants.DEFAULT_MSG_FMT;


    private String srcTerminalId = GlobalConstants.EMPTY_STRING;
    private short srcTerminalType = 0;
    private short registeredDelivery = 0;

    private short msgLength = 140;
    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE;
    private String linkId = GlobalConstants.EMPTY_STRING;
    private String Reserved = GlobalConstants.EMPTY_STRING;

    //短信的文字内容
    private SmsMessage msg;

    /**
     * 状态报告
     */
    private CmppReportRequestMessage reportRequestMessage = null;
    private String batchNumber;

    private short pkTotal = 1;
    private short pkNumber = 1;

    public CmppDeliverRequestMessage() {
        super(CmppPackageType.DELIVERREQUEST);
    }

    public CmppDeliverRequestMessage(CmppHeader header) {
        super(CmppPackageType.DELIVERREQUEST, header);
    }

    @Override
    public int getBodyLength30() {
        if (isReport()) {
            return 168;
        }
        return 97 + getMsgLength();
    }

    @Override
    public int getBodyLength20() {
        if (isReport()) {
            return 133;
        }
        return 73 + getMsgLength();
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

    public String getDestId() {
        return destId;
    }

    @Override
    public String getMsgSignature() {
        return null;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
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

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }

    public short getSrcTerminalType() {
        return srcTerminalType;
    }

    public void setSrcTerminalType(short srcTerminalType) {
        this.srcTerminalType = srcTerminalType;
    }

    public short getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
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

    public String getReserved() {
        return Reserved;
    }

    public void setReserved(String reserved) {
        Reserved = reserved;
    }

    public SmsMessage getMsg() {
        return msg;
    }

    public void setMsg(SmsMessage msg) {
        this.msg = msg;
    }

    /**
     * 设置文本短信内容用于发送
     */
    public void setMsgContent(String msgContent) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        SmsTextMessage smsTextMessage = CommonUtil.buildTextMessage(msgContent);
        setMsgFmt((SmsDcs) smsTextMessage.getDcs());
        setMsg(smsTextMessage);
    }

    /**
     * 设置文本短信内容用于发送,同时设置内容编码格式
     */
    public void setMsgContent(String msgContent, SmsDcs msgFmt) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        setMsgFmt(msgFmt);
        setMsg(CommonUtil.buildTextMessage(msgContent, msgFmt));
    }

    public CmppReportRequestMessage getReportRequestMessage() {
        return reportRequestMessage;
    }

    public void setReportRequestMessage(CmppReportRequestMessage reportRequestMessage) {
        this.reportRequestMessage = reportRequestMessage;
    }

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

    /**
     * 生成一个片段短信
     *
     * @param slice
     * @param sequenceId
     * @return
     * @throws Exception
     */
    @Override
    public CmppDeliverRequestMessage generateMessage(LongMessageSlice slice, int sequenceId) throws Exception {
        CmppDeliverRequestMessage requestMessage = (CmppDeliverRequestMessage) this.clone();
        requestMessage.setTpUdhi(slice.getTpUdhi());
        requestMessage.setMsgFmt((SmsDcs) slice.getMsgFmt());
        requestMessage.setMsgContentBytes(slice.getMsgContentBytes());
        requestMessage.setMsgLength(slice.getMsgLength());
        requestMessage.getHeader().setSequenceId(sequenceId);
        requestMessage.setMsg(null);
        return requestMessage;
    }

    @Override
    public SmsMessage getSmsMessage() {
        return msg;
    }

    @Override
    public boolean isReport() {
        return this.registeredDelivery == 1;
    }

    @Override
    public boolean isLongMsg() {
        return this.tpUdhi == 1;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
    }

    /**
     * 保存了所有的长短信的片段信息
     */
    private List<CmppDeliverRequestMessage> fragments = null;

    @Override
    public List<CmppDeliverRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(CmppDeliverRequestMessage fragment) {
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
    public int getSequenceNum() {
        return super.getSequenceId();
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isReport()) {
            sb.append("CmppDeliverRequestMessage [msgId=").append(msgId).append(", destId=").append(destId).append(", srcTerminalId=").append(srcTerminalId)
                    .append(", getHeader()=").append(getHeader()).append(", ReportRequest=").append(getReportRequestMessage()).append("]");
            return sb.toString();
        }
        sb.append("CmppDeliverRequestMessage [msgId=").append(msgId).append(", destId=").append(destId).append(", srcTerminalId=").append(srcTerminalId)
                .append(", msgContent=").append(getMsgContent()).append(", sequenceId=").append(getHeader().getSequenceId()).append("]");
        return sb.toString();
    }
}
