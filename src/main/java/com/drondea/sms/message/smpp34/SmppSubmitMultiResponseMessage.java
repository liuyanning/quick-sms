package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: SmppSubmitMultiResquestMessage 的响应
 * @author: gengjinbiao
 * @date: 2020年07月07日10:29
 **/
public class SmppSubmitMultiResponseMessage extends AbstractSmppMessage {

    public SmppSubmitMultiResponseMessage() {
        super(SmppPackageType.SUBMITMULTIRESPONSE);
    }

    public SmppSubmitMultiResponseMessage(SmppHeader header) {
        super(SmppPackageType.SUBMITMULTIRESPONSE, header);
    }


    private String messageId;

    //未成功提交给MC的到达目标SME地址的消息数量。后面是指定的未成功SME数量，每个未成功SME在unsuccess_sme字段中指定。
    private short noUnsuccess;

    //失败的SME(复合字段)该字段是一个复合字段，包含一个SME地址(dest_addr_ton、dest_addr_npi和destination_addr)和一个错误代码(error_status_code)。此外，可以根据no_unsuccess字段中指定的值对该字段进行多次编码
    private short destAddrTon;
    private short destAddrNpi;
    private String destinationAddr;
    private Integer errorStatusCode;

    @Override
    public int getBodyLength() {
        int bodyLength = 7;
        bodyLength += getStringLengthPlusOne(this.messageId);
        bodyLength += getStringLengthPlusOne(this.destinationAddr);
        return bodyLength;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public short getNoUnsuccess() {
        return noUnsuccess;
    }

    public void setNoUnsuccess(short noUnsuccess) {
        this.noUnsuccess = noUnsuccess;
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

    public Integer getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(Integer errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    @Override
    public String toString() {
        return "SmppSubmitMultiResponseMessage{" +
                "header='" + getHeader().toString() + '\'' +
                "messageId='" + messageId + '\'' +
                ", noUnsuccess=" + noUnsuccess +
                ", destAddrTon=" + destAddrTon +
                ", destAddrNpi=" + destAddrNpi +
                ", destinationAddr='" + destinationAddr + '\'' +
                ", errorStatusCode=" + errorStatusCode +
                '}';
    }
}
