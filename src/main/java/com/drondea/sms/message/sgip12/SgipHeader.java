package com.drondea.sms.message.sgip12;

import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IPackageType;

import java.io.Serializable;

/**
 * @version V3.0.0
 * @description: sgip协议的header部分
 * @author: liyuehai
 * @date: 2020年07月07日10:38
 **/
public class SgipHeader implements IHeader, Cloneable, Serializable {

    private int messageLength;
    private int headLength = 20;
    private int bodyLength;
    private int commandId;
    private SgipSequenceNumber sequenceNumber;

    public SgipHeader(int messageLength, int commandId, SgipSequenceNumber sequenceNumber) {
        this.messageLength = messageLength;
        this.commandId = commandId;
        this.sequenceNumber = sequenceNumber;
        this.bodyLength = this.messageLength - this.headLength;
    }

    public SgipHeader(IPackageType packageType) {
        this.commandId = packageType.getCommandId();
    }

    public SgipHeader(int commandId) {
        this.commandId = commandId;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
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

    public SgipSequenceNumber getSequenceNumber() {
        return sequenceNumber;
    }

    public long getSequenceId() {
        return sequenceNumber.toLong();
    }

    public long getSequenceNodeId() {
        return sequenceNumber.getNodeId();
    }

    public void setSequenceNumber(SgipSequenceNumber sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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
    public SgipHeader clone() throws CloneNotSupportedException {
        return (SgipHeader) super.clone();
    }
    @Override
    public String toString() {
        return "[commandId=" + commandId
                + ",sequenceNumber=" + sequenceNumber.toString() + "]";
    }

}
