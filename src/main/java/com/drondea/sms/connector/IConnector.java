package com.drondea.sms.connector;

import com.drondea.sms.conf.SocketConfig;

/**
 * @version V3.0.0
 * @description: 连接器接口
 * @author: 刘彦宁
 * @date: 2020年06月05日11:17
 **/
public interface IConnector {

    /**
     * 客户端连接服务器，服务器绑定本地端口
     *
     * @param config
     */
    public void bind(SocketConfig config);

    /**
     * 判断是否已经开启
     *
     * @return
     */
    public boolean isStarted();

    /**
     * 判断是否已经停止
     *
     * @return
     */
    public boolean isStopped();

    /**
     * 关闭所有连接
     */
    public void stop();

    /**
     * 判断是否已经销毁
     *
     * @return
     */
    public boolean isDestroyed();

    /**
     * 销毁所有连接关闭连接器
     */
    public void destroy();
}
