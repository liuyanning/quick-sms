package com.drondea.sms.message.sgip12;

/**
 * @version V3.0.0
 * @description: sgip连接请求回应包
 * @author: liyuehai
 * @date: 2020年07月07日18:36
 **/
public class SgipBindResponseMessage extends AbstractSgipMessage {

    /**
     * 结果，1字节
     */
    private short result = 0;
    /**
     * 扩展，8字节
     */
    private String reserve = "";

    public SgipBindResponseMessage() {
        super(SgipPackageType.BINDRESPONSE);
    }

    public SgipBindResponseMessage(SgipHeader header) {
        super(SgipPackageType.BINDRESPONSE, header);
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
    public String toString() {
        return "SgipBindResponseMessage [result="+result+", reserve="+reserve+", header="+getHeader().toString()+"]";
    }
}
