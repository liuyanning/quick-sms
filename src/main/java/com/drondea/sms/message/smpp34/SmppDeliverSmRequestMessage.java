package com.drondea.sms.message.smpp34;


import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.SmppConstants;
import com.drondea.sms.thirdparty.SmppSmsDcs;
import com.drondea.sms.thirdparty.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: deliver_sm由MC发出，用于向ESME发送消息。使用此命令，MC可以将一条短消息路由到ESME进行传递
 * Deliver_sm是与Submit_sm相反的对称符号，MC用来将消息传递到接收者或收发器ESME。
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppDeliverSmRequestMessage extends AbstractSmppMessage implements ILongSMSMessage<SmppDeliverSmRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SmppDeliverSmRequestMessage.class);

    public SmppDeliverSmRequestMessage() {
        super(SmppPackageType.DELIVERSMREQUEST);
    }

    public SmppDeliverSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.DELIVERSMREQUEST, header);
    }

    /**
     * 参数service_type可用于指示与消息关联的SMS应用程序服务。指定service_type允许ESME使用增强的消息传递服务，
     * 如“替换为service_type”或控制air接口上使用的teleservice。如果不知道，设置为空
     */
    private String serviceType;
    private short sourceAddrTon;
    private short sourceAddrNpi;
    private String sourceAddr;

    private short destAddrTon;
    private short destAddrNpi;
    //此短消息的目的地址为移动终端消息，这是收件人的目录号码MS
    private String destinationAddr;

    private short esmClass;
    private short protocolId;
    private short priorityFlag;

    private String scheduleDeliveryTime;
    private String validityPeriod;
    private short registeredDelivery;
    private short replaceIfPresentFlag;
    private short dataCoding;
    private short smDefaultMsgIid;
    private short smLength;
    private byte[] shortMessage;
    private SmsMessage msg;


    private short pkTotal = 1;
    private short pkNumber = 1;

    private String batchNumber;

    private SmppReportRequestMessage reportRequest = null;

    @Override
    public int getBodyLength() {
        int bodyLength = 12; //各个short类型
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

    public byte[] getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(byte[] shortMessage) {
        this.shortMessage = shortMessage;
    }

    public SmsMessage getMsg() {
        return msg;
    }

    public void setMsg(SmsMessage msg) {
        this.msg = msg;
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

    /**
     * 设置文本短信内容用于发送
     */
    public void setMsgContent(String msgContent) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        SmsTextMessage smsTextMessage = CommonUtil.buildSmppTextMessage(msgContent);
        setDataCoding(smsTextMessage.getDcs().getValue());
        setMsg(smsTextMessage);
    }

    public void setMsgContent(String msgContent, short dataCoding) {
        if (isReport()) {
            logger.error("回执不能设置内容");
            return;
        }
        setDataCoding(dataCoding);
        setMsg(CommonUtil.buildTextMessage(msgContent, new SmppSmsDcs((byte) dataCoding)));
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
    public SmppDeliverSmRequestMessage generateMessage(LongMessageSlice slice, int sequenceId) throws Exception {
        SmppDeliverSmRequestMessage requestMessage = (SmppDeliverSmRequestMessage) this.clone();
        requestMessage.setPkNumber(slice.getPkNumber());
        requestMessage.setPkTotal(slice.getPkTotal());
        if (slice.getTpUdhi() == 1) {
            requestMessage.setEsmClass((short) 0x40);
        } else {
            requestMessage.setEsmClass((short) 0);
        }

        requestMessage.setShortMessage(slice.getMsgContentBytes());
        requestMessage.setSmLength(slice.getMsgLength());
        requestMessage.setDataCoding(slice.getMsgFmt().getValue());
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
        return (esmClass & SmppConstants.ESM_CLASS_MT_MASK) == SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT;
    }

    @Override
    public boolean isLongMsg() {
        return (getEsmClass() & 0x40) == 0x40;
    }

    @Override
    public boolean isMsgComplete() {
        return msg != null;
    }

    /**
     * 保存了所有的长短信的片段信息
     */
    private List<SmppDeliverSmRequestMessage> fragments = null;

    @Override
    public List<SmppDeliverSmRequestMessage> getFragments() {
        return fragments;
    }

    @Override
    public void addFragment(SmppDeliverSmRequestMessage fragment) {
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

    @Override
    public String getMsgSignature() {
        return null;
    }

    public SmppReportRequestMessage getReportRequest() {
        return reportRequest;
    }

    public void setReportRequest(SmppReportRequestMessage reportRequest) {
        //设置report说明此消息是回执需要设置esmclass
        this.setEsmClass(SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT);
        byte[] messageByte = reportRequest.getMessageByte();
        this.setShortMessage(messageByte);
        this.setSmLength((short) messageByte.length);
        this.reportRequest = reportRequest;
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
        return false;
    }

    @Override
    public String toString() {
        return "SmppDeliverSmRequestMessage{" +
                "header=" + this.getHeader().toString() +
                ", serviceType='" + serviceType + '\'' +
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
                ", shortMessage=" + getMsgContent() +
//                ", OptionalParameters=" + getOptionalParameters()==null?"":getOptionalParameters().toString() +
                '}';
    }
}
