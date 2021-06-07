
package com.drondea.sms.message;

import com.drondea.sms.channel.ChannelSession;

import java.util.List;

/**
 * @author liuyanning
 */
public interface MessageProvider {

    /**
     * 提供消息的接口
     * @param channelSession
     * @return
     */
    List<IMessage> getTcpMessages(ChannelSession channelSession);

    /**
     * 没有匹配到滑动窗口执行
     * @param requestKey
     * @param response
     */
    void responseMessageMatchFailed(String requestKey, IMessage response);
}
