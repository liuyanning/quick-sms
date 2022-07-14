package com.drondea.sms.connector.cmpp;

import com.drondea.sms.conf.cmpp.CmppClientSocketConfig;
import com.drondea.sms.connector.AbstractClientConnector;
import com.drondea.sms.handler.SessionHandler;
import com.drondea.sms.handler.transcoder.Cmpp20MessageCodec;
import com.drondea.sms.handler.transcoder.Cmpp30MessageCodec;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @version V3.0.0
 * @description: cmpp连接器
 * @author: 刘彦宁
 * @date: 2020年06月08日09:45
 **/
public class CmppClientConnector extends AbstractClientConnector {

    public CmppClientConnector(SessionManager sessionManager) {
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
        CmppClientSocketConfig socketConfig = (CmppClientSocketConfig) (getSessionManager().getSocketConfig());
        //日志处理
        pipeline.addLast("LoggingHandler", new LoggingHandler(String.format(GlobalConstants.BYTE_LOG_PREFIX, socketConfig.getId()), LogLevel.DEBUG));
        //粘包处理,CMPP的
        pipeline.addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024, 0, 4, -4, 0, true));

        short version = socketConfig.getVersion();
        if (version < CmppConstants.VERSION_30) {
            //打包、解包
            pipeline.addLast("CmppMessageCodec", Cmpp20MessageCodec.getInstance());
        } else {
            pipeline.addLast("CmppMessageCodec", Cmpp30MessageCodec.getInstance());
        }
        //记录日志
        pipeline.addLast("MessageLogHandler", GlobalConstants.MESSAGE_LOG_HANDLER);
        //session管理
        pipeline.addLast("SessionHandler", new SessionHandler(getSessionManager()));
    }
}
