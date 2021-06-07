package com.drondea.sms.common;

/**
 * @author 27581
 */
public interface SequenceNumber {

    /**
     * 获取下一个number
     * @return
     */
    int next();

    /**
     * 获取当前值
     * @return
     */
    int getValue();
}
