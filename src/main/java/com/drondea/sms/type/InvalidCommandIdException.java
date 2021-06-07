package com.drondea.sms.type;

import com.drondea.sms.message.IHeader;
import com.drondea.sms.message.smpp34.SmppHeader;

/**
 * 不合法的请求命令
 */
public class InvalidCommandIdException extends Exception {

    private IHeader header;

    public InvalidCommandIdException(String message) {
        super(message);
    }

    public InvalidCommandIdException(String message, SmppHeader header) {
        super(message);
        this.header = header;
    }

    public IHeader getHeader() {
        return header;
    }

    public void setHeader(IHeader header) {
        this.header = header;
    }
}
