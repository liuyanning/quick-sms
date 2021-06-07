package com.drondea.sms.message.sgip12;

/**
 * @version V3.0.0
 * @description: 断开连接的响应
 * @author: liyuehai
 * @date: 2020年06月17日17:12
 **/
public class SgipUnbindResponseMessage extends AbstractSgipMessage {

    public SgipUnbindResponseMessage() {
        super(SgipPackageType.UNBINDRESPONSE);
    }

    public SgipUnbindResponseMessage(SgipHeader header) {
        super(SgipPackageType.UNBINDRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 0;
    }
    @Override
    public String toString() {
        return String
                .format("SgipUnbindResponseMessage [header=%s]",getHeader().toString());
    }
}
