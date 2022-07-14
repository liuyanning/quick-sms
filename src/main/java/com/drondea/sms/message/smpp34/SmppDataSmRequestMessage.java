package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: data_sm操作与submit_sm类似，因为它提供了提交移动终止消息的方法。
 * 然而，data_sm是为基于包的应用程序(如WAP)设计的，因为它提供了一个简化的PDU主体，
 * 其中包含与WAP或基于包的应用程序相关的字段。
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppDataSmRequestMessage extends AbstractSmppMessage {

    public SmppDataSmRequestMessage() {
        super(SmppPackageType.DATASMREQUEST);
    }

    public SmppDataSmRequestMessage(SmppHeader header) {
        super(SmppPackageType.DATASMREQUEST, header);
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

    private short esmClass;
    private short registeredDelivery;
    private short dataCoding;

    @Override
    public int getBodyLength() {
        int bodyLength = 7; //各个short类型
        bodyLength += getStringLengthPlusOne(this.serviceType);
        bodyLength += getStringLengthPlusOne(this.sourceAddr);
        bodyLength += getStringLengthPlusOne(this.destinationAddr);
        return bodyLength;
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

    public short getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public short getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(short dataCoding) {
        this.dataCoding = dataCoding;
    }

    @Override
    public String toString() {
        return "SmppDataSmRequestMessage{" +
                "header=" + this.getHeader().toString() +
                ", serviceType='" + serviceType + '\'' +
                ", sourceAddrTon=" + sourceAddrTon +
                ", sourceAddrNpi=" + sourceAddrNpi +
                ", sourceAddr='" + sourceAddr + '\'' +
                ", destAddrTon=" + destAddrTon +
                ", destAddrNpi=" + destAddrNpi +
                ", destinationAddr='" + destinationAddr + '\'' +
                ", esmClass=" + esmClass +
                '}';
    }
}
