package com.drondea.sms.message.smpp34;

import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.type.SmppConstants;

import java.io.Serializable;

/**
 * @version V3.0.0
 * @description: smpp协议的header部分
 * @author: gengjinbiao
 * @date: 2020年07月03日10:29
 **/
public class SmppHeader implements IHeader, Cloneable, Serializable {

    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String resultMessage;

    public SmppHeader(IPackageType packageType) {
        this.commandId = packageType.getCommandId();
    }

    public SmppHeader(int commandLength, int commandId, int commandStatus, int sequenceNumber) {
        this.commandLength = commandLength;
        this.commandId = commandId;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public void setCommandLength(int commandLength) {
        this.commandLength = commandLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getCommandStatus() {
        return commandStatus;
    }

    public void setCommandStatus(int commandStatus) {
        this.commandStatus = commandStatus;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getResultMessage() {
        return SmppConstants.STATUS_MESSAGE_MAP.get(new Integer(commandStatus));
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public boolean isRequest() {
        return false;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    @Override
    public SmppHeader clone() throws CloneNotSupportedException {
        return (SmppHeader) super.clone();
    }

    @Override
    public String toString() {
        return "SmppHeader{" +
                "commandLength=" + commandLength +
                ", commandId=" + commandId +
                ", commandStatus=" + commandStatus +
                ", sequenceNumber=" + sequenceNumber +
                '}';
    }
}
