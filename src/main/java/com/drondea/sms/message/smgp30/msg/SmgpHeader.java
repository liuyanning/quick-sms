package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IPackageType;

import java.io.Serializable;

/**
 * @version V3.0
 * @description: smgp协议的header部分
 * @author: ywj
 * @date: 2020年06月08日10:29
 **/
public class SmgpHeader implements IHeader, Cloneable, Serializable {

    private int totalLength;
    public int headLength = 12;//头长度
    private int bodyLength;
    private int commandId;
    private int sequenceId;

    public SmgpHeader(int totalLength, int commandId, int sequenceId) {
        this.totalLength = totalLength;
        this.commandId = commandId;
        this.sequenceId = sequenceId;
        this.bodyLength = this.totalLength - headLength;
    }

    public SmgpHeader(IPackageType packageType) {
        this.commandId = packageType.getCommandId();
    }

    public SmgpHeader(int commandId) {
        this.commandId = commandId;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    @Override
    public boolean isRequest() {
        return (commandId & 0x80000000L) == 0L;
    }

    @Override
    public boolean isResponse() {
        return (commandId & 0x80000000L) == 0x80000000L;
    }

    @Override
    public SmgpHeader clone() throws CloneNotSupportedException {
        return (SmgpHeader) super.clone();
    }
}
