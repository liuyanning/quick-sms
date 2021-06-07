package com.drondea.sms.type;

/**
 * session相关的事件处理
 *
 * @author 27581
 */
public interface ISessionEventHandler {

    /**
     * session状态改变触发
     *
     * @param writeable
     */
    void sessionWritablityChanged(boolean writeable);
}
