package com.drondea.sms.connector.sgip;

import com.drondea.sms.conf.sgip.SgipServerSocketConfig;
import com.drondea.sms.connector.AbstractServerConnector;
import com.drondea.sms.handler.SessionHandler;
import com.drondea.sms.handler.transcoder.Sgip12MessageCodec;
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
 * @description: Sgip服务器端连接器
 * @author: liyuehai
 * @date: 2020年06月10日13:44
 **/
public class SgipServerConnector extends AbstractServerConnector {

    public SgipServerConnector(SessionManager sessionManager) {
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
        SgipServerSocketConfig socketConfig = (SgipServerSocketConfig) (getSessionManager().getSocketConfig());
        //日志处理
        pipeline.addLast("LoggingHandler", new LoggingHandler(String.format(GlobalConstants.BYTE_LOG_PREFIX, socketConfig.getId()), LogLevel.DEBUG));
        //粘包处理,Sgip的
        pipeline.addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024, 0, 4, -4, 0, true));
        //打包、解包
        pipeline.addLast("SgipMessageCodec", Sgip12MessageCodec.getInstance());
        //记录日志
        pipeline.addLast("MessageLogHandler", GlobalConstants.MESSAGE_LOG_HANDLER);
        //session管理
        pipeline.addLast("SessionHandler", new SessionHandler(getSessionManager()));
    }
}
