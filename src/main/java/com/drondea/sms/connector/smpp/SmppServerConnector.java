package com.drondea.sms.connector.smpp;

import com.drondea.sms.conf.smpp.SmppServerSocketConfig;
import com.drondea.sms.connector.AbstractServerConnector;
import com.drondea.sms.handler.SessionHandler;
import com.drondea.sms.handler.transcoder.Smpp34MessageCodec;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.GlobalConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @version V3.0.0
 * @description: smpp服务器端连接器
 * @author: gengjinbiao
 * @date: 2020年07月15日13:44
 **/
public class SmppServerConnector extends AbstractServerConnector {

    public SmppServerConnector(SessionManager sessionManager) {
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
        SmppServerSocketConfig socketConfig = (SmppServerSocketConfig) (getSessionManager().getSocketConfig());
        //日志处理
        pipeline.addLast("LoggingHandler", new LoggingHandler(String.format(GlobalConstants.BYTE_LOG_PREFIX, socketConfig.getId()), LogLevel.DEBUG));

        //粘包处理,SMPP的
        pipeline.addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024, 0, 4, -4, 0, true));

        pipeline.addLast("SmppMessageCodec", Smpp34MessageCodec.getInstance());
        //记录日志
        pipeline.addLast("MessageLogHandler", GlobalConstants.MESSAGE_LOG_HANDLER);
        //session管理
        pipeline.addLast("SessionHandler", new SessionHandler(getSessionManager()));
    }
}
