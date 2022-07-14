package com.drondea.sms.message.sgip12;

import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.type.GlobalConstants;

/**
 * @version V3.0.0
 * @description: 发送回执
 * @author: liyuehai
 * @date: 2020年06月08日10:49
 **/
public class SgipReportRequestMessage extends AbstractSgipMessage {
    private SgipSequenceNumber submitSequenceNumber;
    private short reportType = 0;
    private String userNumber = null;
    private short state = 0;
    private short errorCode = 0;
    private String reserve = GlobalConstants.EMPTY_STRING;


    public SgipReportRequestMessage() {
        super(SgipPackageType.REPORTREQUEST);
    }

    public SgipReportRequestMessage(SgipHeader header) {
        super(SgipPackageType.REPORTREQUEST, header);
    }

    public SgipSequenceNumber getSubmitSequenceNumber() {
        return submitSequenceNumber;
    }

    public void setSubmitSequenceNumber(SgipSequenceNumber submitSequenceNumber) {
        this.submitSequenceNumber = submitSequenceNumber;
    }

    public short getReportType() {
        return reportType;
    }

    public void setReportType(short reportType) {
        this.reportType = reportType;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    @Override
    public int getBodyLength() {
        return 44;
    }

    @Override
    public boolean isWindowSendMessage() {
        return true;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SgipReportRequestMessage [submitSequenceNumber=").append(submitSequenceNumber.toString()).
                append(", reportType=").append(reportType).append(", userNumber=").append(userNumber)
                .append(", state=").append(state).append(", errorCode=").append(errorCode).append(", reserve=").append(reserve)
                .append(", header=").append(getHeader().toString()).append("]");
        return sb.toString();
    }
}
