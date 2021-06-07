package com.drondea.sms.type;


/**
 * 记录日志
 */
public class LoggingOptions {

    public static final int LOG_MSG = 0x00000001;
    public static final int LOG_BYTES = 0x00000002;
    public static final int DEFAULT_LOG_OPTION = LOG_MSG;

    private int option;

    public LoggingOptions() {
        this.option = DEFAULT_LOG_OPTION;
    }

    public void setLogPdu(boolean value) {
        if (value) {
            this.option |= LOG_MSG;
        } else {
            this.option &= ~LOG_MSG;
        }
    }

    public boolean isLogPduEnabled() {
        return ((this.option & LOG_MSG) > 0);
    }

    public void setLogBytes(boolean value) {
        if (value) {
            this.option |= LOG_BYTES;
        } else {
            this.option &= ~LOG_BYTES;
        }
    }

    public boolean isLogBytesEnabled() {
        return ((this.option & LOG_BYTES) > 0);
    }
}
