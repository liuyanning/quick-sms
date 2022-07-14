package com.drondea.sms.message.sgip12;


/**
 * @version V3.0.0
 * @description: sgip公共回应包
 * @author: liyuehai
 * @date: 2020年07月07日18:36
 **/
public class SgipSubmitResponseMessage extends AbstractSgipMessage {

    /**
     * 结果，1字节
     */
    private short result = 0;
    /**
     * 扩展，8字节
     */
    private String reserve = "";

    public SgipSubmitResponseMessage() {
        super(SgipPackageType.SUBMITRESPONSE);
    }

    public SgipSubmitResponseMessage(SgipHeader header) {
        super(SgipPackageType.SUBMITRESPONSE, header);
    }

    public short getResult() {
        return result;
    }

    public void setResult(short result) {
        this.result = result;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    @Override
    public int getBodyLength() {
        return 9;
    }

    @Override
    public boolean isWindowResponseMessage() {
        return true;
    }
    @Override
    public String toString() {
        return "SgipSubmitResponseMessage [result="+ result +",reserve="+ reserve +",header="+getHeader().toString()+"]";
    }
}
