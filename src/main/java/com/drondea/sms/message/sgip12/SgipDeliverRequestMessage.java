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
import java.util.List;

/**
 * @version V3.0.0
 * @description: Sgip上行短信
 * @author: liyuehai
 * @date: 2020年06月08日10:49
 **/
public class SgipDeliverRequestMessage extends AbstractSgipMessage implements ILongSMSMessage<SgipDeliverRequestMessage> {

    private String userNumber = null;
    private String spNumber = GlobalConstants.EMPTY_STRING;
    private short tpPid = 0;
    private short tpUdhi = 0;
    private SmsDcs messageCoding = SgipConstants.DEFAULT_MSG_FMT;
    private int messageLength = 120;
    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE;
    private String reserve = GlobalConstants.EMPTY_STRING;

    private SmsMessage msg;
    private String batchNumber;
    private short pkTotal = 1;
    private short pkNumber = 1;

    public SgipDeliverRequestMessage() {
        super(SgipPackageType.DELIVERREQUEST);
    }

    public SgipDeliverRequestMessage(SgipHeader header) {
        super(SgipPackageType.DELIVERREQUEST, header);
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public SmsMessage getMsg() {
        return msg;
    }

    public void setFragments(List<SgipDeliverRequestMessage> fragments) {
        this.fragments = fragments;
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

    @Override
    public int getBodyLength() {
        return 57 + getMessageLength();
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
    public SgipDeliverRequestMessage generateMessage(LongMessageSlice frame, int sequenceNumber) throws Exception {
        SgipDeliverRequestMessage requestMessage = (SgipDeliverRequestMessage) this.clone();
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
    private List<SgipDeliverRequestMessage> fragments = null;

    @Override
    public List<SgipDeliverRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SgipDeliverRequestMessage fragment) {
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SgipDeliverRequestMessage [").append("userNumber=").append(userNumber).append(", spNumber=").append(spNumber)
                .append(", messageContent=").append(getMsgContent()).append(", header=").append(getHeader().toString()).append("]");
        return sb.toString();
    }
}
