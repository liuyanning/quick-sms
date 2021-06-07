package com.drondea.sms.connector.smgp;

import com.drondea.sms.conf.smgp.SmgpServerSocketConfig;
import com.drondea.sms.connector.AbstractServerConnector;
import com.drondea.sms.handler.SessionHandler;
import com.drondea.sms.handler.transcoder.Smgp30MessageCodec;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.GlobalConstants;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @version V3.0
 * @description: smgp服务器端连接器
 * @author: ywj
 * @date: 2020年06月10日13:44
 **/
public class SmgpServerConnector extends AbstractServerConnector {

    public SmgpServerConnector(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected ChannelInitializer<?> buildChannelInitializer() {
        return new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                setInitHandler(pipeline);
            }
        };
    }

    /**
     * 设置handler
     *
     * @param pipeline
     */
    public void setInitHandler(ChannelPipeline pipeline) {
        SmgpServerSocketConfig socketConfig = (SmgpServerSocketConfig) (getSessionManager().getSocketConfig());
        //日志处理
        pipeline.addLast("LoggingHandler", new LoggingHandler(String.format(GlobalConstants.BYTE_LOG_PREFIX, socketConfig.getId()), LogLevel.DEBUG));

        //粘包处理,SMGP粘包处理与 CMPP一致
        pipeline.addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024, 0, 4, -4, 0, true));

        //打包、解包
        pipeline.addLast("SmgpMessageCodec", Smgp30MessageCodec.getInstance());

        //session管理
        pipeline.addLast("SessionHandler", new SessionHandler(getSessionManager()));
    }
}
