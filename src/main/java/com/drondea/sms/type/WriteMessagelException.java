package com.drondea.sms.type;

/**
 * @version V3.0.0
 * @description: 写channel失败异常，需要重写
 * @author: 刘彦宁
 * @date: 2020年06月24日10:37
 **/
public class WriteMessagelException extends Exception {

    public WriteMessagelException(String message) {
        super(message);
    }
}
