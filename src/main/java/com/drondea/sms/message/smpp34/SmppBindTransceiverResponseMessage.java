package com.drondea.sms.message.smpp34;


import static com.drondea.sms.common.util.SmppUtil.getStringLengthPlusOne;

/**
 * @version V3.0.0
 * @description: smpp协议的 BindTransceiver 绑定为收发器响应包
 * @author: gengjinbiao
 * @date: 2020年07月03日10:29
 **/
public class SmppBindTransceiverResponseMessage extends AbstractSmppMessage {

    public SmppBindTransceiverResponseMessage() {
        super(SmppPackageType.BINDTRANSCEIVERRESPONSE);
    }

    public SmppBindTransceiverResponseMessage(SmppHeader header) {
        super(SmppPackageType.BINDTRANSCEIVERRESPONSE, header);
    }

    private String systemId;


    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }


    @Override
    public int getBodyLength() {
        return getStringLengthPlusOne(this.systemId);
    }


    @Override
    public String toString() {
        return "body{" + getHeader().toString() +
                ",systemId='" + systemId + '\'' +
//                "OptionalParameters='" + getOptionalParameters().toString() + '\'' +
                '}';
    }
}
