package com.drondea.sms.session;

import com.codahale.metrics.Timer;
import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.message.MessageProvider;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: 连接管理
 * @author: 刘彦宁
 * @date: 2020年06月05日14:24
 **/
public interface SessionManager {

    /**
     * 创建一个session,持有channel
     *
     * @param ctx
     * @return
     */
    ChannelSession createSession(ChannelHandlerContext ctx);

    /**
     * 建立连接或者绑定端口
     */
    void doOpen();

    /**
     * 关闭所有的连接
     */
    void doClose();

    /**
     * 对客户端的所有连接进行检查
     */
    void doCheckSessions();

    /**
     * 获取配置项
     *
     * @return
     */
    SocketConfig getSocketConfig();

    /**
     * 添加session
     *
     * @param session
     */
    void addSession(ChannelSession session);

    /**
     * 移除session
     *
     * @param session
     */
    void removeSession(ChannelSession session);

    /**
     * 获取session的size
     *
     * @return
     */
    int getSessionSize();

    /**
     * 获取客户定制处理器
     *
     * @return
     */
    ICustomHandler getCustomHandler();

    IChannelSessionCounters createSessionCounters();

    /**
     * 获取消息提供者
     * @return
     */
    MessageProvider getMessageProvider();

    Timer getWindowTimer();

    /**
     * 设置消息提供者
     * @param msgProvider
     */
    void setMessageProvider(MessageProvider msgProvider);
}
