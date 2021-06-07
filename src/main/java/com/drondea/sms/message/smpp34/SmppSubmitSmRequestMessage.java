package com.drondea.sms.message.smpp34;


import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmppSmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.InvalidMessageException;

import java.util.ArrayList;
import java.util.List;

import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: 消息提交操作提供了一个ESME，能够提交消息，以便继续交付到移动站
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppSubmitSmRequestMessage extends AbstractSmppMessage implements ILongSMSMessage<SmppSubmitSmRequestMessage> {

    public SmppSubmitSmRequestMessage() {
        super(SmppPackageType.SUBMITSMREQUEST);
    }

    public SmppSubmitSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.SUBMITSMREQUEST, header);
    }

    /**
     * service_type参数可用于指示SMS与消息关联的应用程序服务。指定service_type允许
     * ESME使用增强的消息传递服务，如“替换为service_type”或控制空中接口上使用的teleservice。
     * 默认MC设置为NULL
     */
    private String serviceType;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private short destAddrTon;
    private short destAddrNpi;
    //此短消息的目的地址为移动终端消息，这是收件人的目录号码MS
    private String destinationAddr;
    // 指示消息模式和消息类型
    private short esmClass;
    //协议标识符。网络特定字段。
    private short protocolId;
    // 指定消息的优先级级别
    private short priorityFlag;

    private String scheduleDeliveryTime;
    private String validityPeriod;
    private short registeredDelivery;
    private short replaceIfPresentFlag;
    private short dataCoding;
    private short smDefaultMsgIid;
    private short smLength;
    private short defaultMsgId;
    private byte[] shortMessage;
    private SmsMessage msg;

    private short pkTotal = 1;
    private short pkNumber = 1;

    private String signature;
    private String batchNumber;

    @Override
    public int getBodyLength() {
        //各个short类型
        int bodyLength = 12;
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.destinationAddr);
        bodyLength += getStringLengthPlusOne(this.scheduleDeliveryTime);
        bodyLength += getStringLengthPlusOne(this.validityPeriod);

        bodyLength += this.shortMessage == null ? 0 : this.shortMessage.length;
        return bodyLength;
    }

    @Override
    public boolean isWindowSendMessage() {
        return true;
    }

    /**
     * 保存了所有的长短信的片段信息
     */
    private List<SmppSubmitSmRequestMessage> fragments = null;

    @Override
    public boolean isRequest() {
        return true;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public short getSourceAddrTon() {
        return sourceAddrTon;
    }

    public void setSourceAddrTon(short sourceAddrTon) {
        this.sourceAddrTon = sourceAddrTon;
    }

    public short getSourceAddrNpi() {
        return sourceAddrNpi;
    }

    public void setSourceAddrNpi(short sourceAddrNpi) {
        this.sourceAddrNpi = sourceAddrNpi;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public short getDestAddrTon() {
        return destAddrTon;
    }

    public void setDestAddrTon(short destAddrTon) {
        this.destAddrTon = destAddrTon;
    }

    public short getDestAddrNpi() {
        return destAddrNpi;
    }

    public void setDestAddrNpi(short destAddrNpi) {
        this.destAddrNpi = destAddrNpi;
    }

    public String getDestinationAddr() {
        return destinationAddr;
    }

    public void setDestinationAddr(String destinationAddr) {
        this.destinationAddr = destinationAddr;
    }

    public short getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(short esmClass) {
        this.esmClass = esmClass;
    }

    public short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(short protocolId) {
        this.protocolId = protocolId;
    }

    public short getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(short priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public short getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public short getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    public void setReplaceIfPresentFlag(short replaceIfPresentFlag) {
        this.replaceIfPresentFlag = replaceIfPresentFlag;
    }

    public short getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(short dataCoding) {
        this.dataCoding = dataCoding;
    }

    public short getSmDefaultMsgIid() {
        return smDefaultMsgIid;
    }

    public void setSmDefaultMsgIid(short smDefaultMsgIid) {
        this.smDefaultMsgIid = smDefaultMsgIid;
    }

    public short getSmLength() {
        return smLength;
    }

    public void setSmLength(short smLength) {
        this.smLength = smLength;
    }

    public short getDefaultMsgId() {
        return defaultMsgId;
    }

    public void setDefaultMsgId(short defaultMsgId) {
        this.defaultMsgId = defaultMsgId;
    }

    public byte[] getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(byte[] shortMessage) throws InvalidMessageException {
        if (shortMessage != null && shortMessage.length > 255) {
            throw new InvalidMessageException("A short message in a PDU can only be a max of 255 bytes [actual=" + shortMessage.length, this);
        }
        this.shortMessage = shortMessage;
    }

    public SmsMessage getMsg() {
        return msg;
    }

    public void setMsg(SmsMessage msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SmppSubmitRequestMessage{" +
                "header=" + this.getHeader().toString() +
                "serviceType='" + serviceType + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", destAddrTon=" + destAddrTon +
                ", destAddrNpi=" + destAddrNpi +
                ", destinationAddr='" + destinationAddr + '\'' +
                ", esmClass=" + esmClass +
                ", protocolId=" + protocolId +
                ", priorityFlag=" + priorityFlag +
                ", scheduleDeliveryTime='" + scheduleDeliveryTime + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", registeredDelivery=" + registeredDelivery +
                ", replaceIfPresentFlag=" + replaceIfPresentFlag +
                ", dataCoding=" + dataCoding +
                ", smDefaultMsgIid=" + smDefaultMsgIid +
                ", smLength=" + smLength +
                ", defaultMsgId=" + defaultMsgId +
                ", shortMessage=" + getMsgContent() +
                '}';
    }

    @Override
    public LongMessageSlice generateSlice() {
        LongMessageSlice slice = new LongMessageSlice();
        slice.setTpPid(getProtocolId());
        if ((getEsmClass() & 0x40) == 0x40) {
            slice.setTpudhi((short) 1);
        }
        slice.setMsgFmt(new SmppSmsDcs((byte) getDataCoding()));
        slice.setMsgContentBytes(getShortMessage());
        slice.setMsgLength(getSmLength());
        slice.setSequence(getSequenceNum());
        return slice;
    }

    @Override
    public SmppSubmitSmRequestMessage generateMessage(LongMessageSlice frame, int sequenceId) throws Exception {
        SmppSubmitSmRequestMessage requestMessage = (SmppSubmitSmRequestMessage) this.clone();
        requestMessage.setPkNumber(frame.getPkNumber());
        requestMessage.setPkTotal(frame.getPkTotal());
        if (frame.getTpUdhi() == 1) {
            requestMessage.setEsmClass((short) 0x40);
        } else {
            requestMessage.setEsmClass((short) 0);
        }
        requestMessage.setDataCoding(frame.getMsgFmt().getValue());
        requestMessage.setShortMessage(frame.getMsgContentBytes());
        requestMessage.setSmLength(frame.getMsgLength());
        requestMessage.getHeader().setSequenceNumber(sequenceId);
        requestMessage.setMsg(null);

        return requestMessage;
    }

    @Override
    public SmsMessage getSmsMessage() {
        return msg;
    }

    @Override
    public boolean isReport() {
        return false;
    }

    @Override
    public boolean isLongMsg() {
        return (getEsmClass() & 0x40) == 0x40;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
    }

    @Override
    public List<SmppSubmitSmRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SmppSubmitSmRequestMessage fragment) {
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

        if (this.shortMessage != null && this.shortMessage.length > 0) {
            LongMessageSlice slice = generateSlice();
            return LongMessageSliceManager.getPartTextMsg(slice);
        }

        return "";
    }

    @Override
    public int getSequenceNum() {
        return getHeader().getSequenceNumber();
    }

    /**
     * 设置文本短信内容用于发送
     */
    public void setMsgContent(String msgContent) {
        SmsTextMessage smsTextMessage = CommonUtil.buildSmppTextMessage(msgContent);
        setDataCoding(smsTextMessage.getDcs().getValue());
        setMsg(smsTextMessage);
    }

    /**
     * 设置文本短信内容用于发送,同时设置内容编码格式
     */
    public void setMsgContent(String msgContent, short dataCoding) {
        setDataCoding(dataCoding);
        setMsg(CommonUtil.buildTextMessage(msgContent, new SmppSmsDcs((byte) dataCoding)));
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
