package com.drondea.sms.type;

import com.drondea.sms.message.IMessage;

/**
 * 不合法的消息
 */
public class InvalidMessageException extends Exception {

    private IMessage msg;

    public InvalidMessageException(String message, IMessage msg) {
        super(message);
        this.msg = msg;
    }

    public IMessage getMsg() {
        return msg;
    }

    public void setMsg(IMessage msg) {
        this.msg = msg;
    }
}
