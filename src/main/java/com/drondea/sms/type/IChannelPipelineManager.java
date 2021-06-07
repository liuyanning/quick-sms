package com.drondea.sms.type;

import io.netty.channel.ChannelPipeline;

/**
 * 自定义的pipeline管理，可以添加业务handler等
 *
 * @author liuyanning
 */
public interface IChannelPipelineManager {

    /**
     * 对Pipeline进行处理
     *
     * @param pipeline
     */
    public void doPipLine(ChannelPipeline pipeline);
}
