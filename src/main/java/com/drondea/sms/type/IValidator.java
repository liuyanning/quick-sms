package com.drondea.sms.type;

/**
 * @version V3.0.0
 * @description: 获取连接服务器的用户信息
 * @author: 刘彦宁
 * @date: 2020年06月10日17:56
 **/
public interface IValidator {

    /**
     * 获取用户配置信息
     *
     * @param name
     * @return
     */
    UserChannelConfig getUserChannelConfig(String name);
}
