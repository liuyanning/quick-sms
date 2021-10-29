package com.drondea.sms.connector;

import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @version V3.0.0
 * @description: 客户端连接器
 * @author: 刘彦宁
 * @date: 2020年06月05日11:32
 **/
public abstract class AbstractClientConnector implements IConnector {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientConnector.class);

    private Bootstrap clientBootstrap;
    private EventLoopGroup workerGroup;
    private SessionManager sessionManager;

    public AbstractClientConnector(SessionManager sessionManager) {
        this(null, sessionManager);
    }

    public AbstractClientConnector(EventLoopGroup workerGroup, SessionManager connectorManager) {
        //提供默认workerGroup
        if (workerGroup == null) {
            workerGroup = DefaultEventGroupFactory.getInstance().getWorker();
        }
        this.workerGroup = workerGroup;
        this.sessionManager = connectorManager;
        this.clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_RCVBUF, 16384).option(ChannelOption.SO_SNDBUF, 8192)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024))
                .handler(buildChannelInitializer());
    }

    @Override
    public void bind(SocketConfig config) {
        createConnectedChannel(config.getHost(), config.getPort(), config.getConnectTimeout());
    }

    /**
     * 设置Initializer
     *
     * @return
     */
    protected abstract ChannelInitializer<?> buildChannelInitializer();


    protected ChannelFuture createConnectedChannel(String host, int port, long connectTimeoutMillis) {
        // a socket address used to "bind" to the remote system
        InetSocketAddress socketAddr = new InetSocketAddress(host, port);

        // set the timeout
        this.clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeoutMillis);

        // attempt to connect to the remote system
        ChannelFuture connectFuture = this.clientBootstrap.connect(socketAddr);

        logger.debug("attempt to connect to the remote system host:{}, port:{}", host, port);
        return connectFuture;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public boolean isStarted() {
        return sessionManager.getSessionSize() > 0;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void stop() {
        sessionManager.doClose();
    }

    @Override
    public boolean isDestroyed() {
        return this.workerGroup == null;
    }

    @Override
    public void destroy() {
        stop();
        this.workerGroup = null;
    }
}
