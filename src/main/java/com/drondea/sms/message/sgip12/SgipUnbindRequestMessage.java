package com.drondea.sms.message.sgip12;

/**
 * @version V3.0.0
 * @description: 断开连接请求
 * @author: liyuehai
 * @date: 2020年06月17日17:08
 **/
public class SgipUnbindRequestMessage extends AbstractSgipMessage {

    public SgipUnbindRequestMessage() {
        super(SgipPackageType.UNBINDREQUEST);
    }

    public SgipUnbindRequestMessage(SgipHeader header) {
        super(SgipPackageType.UNBINDREQUEST, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
    @Override
    public String toString() {
        return "SgipUnbindRequestMessage [header="+getHeader().toString()+"]";
    }
}
