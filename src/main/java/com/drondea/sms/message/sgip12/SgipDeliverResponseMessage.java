package com.drondea.sms.message.sgip12;

/**
 * @version V3.0.0
 * @description: deliver的响应包
 * @author: liyuehai
 * @date: 2020年06月08日10:50
 **/
public class SgipDeliverResponseMessage extends AbstractSgipMessage {

    /**
     * 结果，1字节
     */
    private short result = 0;
    /**
     * 扩展，8字节
     */
    private String reserve = "";

    public SgipDeliverResponseMessage() {
        super(SgipPackageType.DELIVERRESPONSE);
    }

    public SgipDeliverResponseMessage(SgipHeader header) {
        super(SgipPackageType.DELIVERRESPONSE, header);
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
        return String
                .format("SgipDeliverResponseMessage [result=%s, reserve=%s, header=%s]",
                        result, reserve, getHeader().toString());
    }
}
