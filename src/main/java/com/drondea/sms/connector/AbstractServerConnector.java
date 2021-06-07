package com.drondea.sms.connector;

import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.session.SessionManager;
import com.drondea.sms.type.DefaultEventGroupFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: 服务器端连接器，监听客户端连接
 * @author: 刘彦宁
 * @date: 2020年06月10日11:35
 **/
public abstract class AbstractServerConnector implements IConnector {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServerConnector.class);

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private SessionManager sessionManager;
    private Channel serverChannel;


    public AbstractServerConnector(SessionManager sessionManager) {
        this(null, null, sessionManager);
    }

    public AbstractServerConnector(EventLoopGroup bossGroup, EventLoopGroup workerGroup, SessionManager connectorManager) {
        if (bossGroup == null) {
            bossGroup = DefaultEventGroupFactory.getInstance().getBoss();
        }
        //提供默认workerGroup
        if (workerGroup == null) {
            workerGroup = DefaultEventGroupFactory.getInstance().getWorker();
        }
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.bootstrap = new ServerBootstrap();
        this.sessionManager = connectorManager;

        /**linux环境下
         * SO_BACKLOG 说明：SYN_RECEIVED队列的大小由proc/sys/net/ipv4/tcp_max_syn_backlog系统参数指定默认1024，
         * ESTABLISHED队列由backlog和/proc/sys/net/core/somaxconn(默认128)中较小的指定。这个值会影响建立连接的并发数量
         * SO_RCVBUF 和 SO_SNDBUF 说明： 接收数据缓冲区和发送数据缓冲区，linux2.4 开始这个值系统会自动调整，最小4096 默认87380 最大6291456
         * ALLOCATOR 默认值为ByteBufAllocator.DEFAULT，4.0版本为UnpooledByteBufAllocator，4.1版本为PooledByteBufAllocator。
         * 该值也可以使用系统参数io.netty.allocator.type配置，使用字符串值："unpooled"，"pooled"。
         * RCVBUF_ALLOCATOR 用于Channel分配接受Buffer的分配器，默认值为AdaptiveRecvByteBufAllocator.DEFAULT，
         * 是一个自适应的接受缓冲区分配器，能根据接受到的数据自动调节大小。可选值为FixedRecvByteBufAllocator，固定大小的接受缓冲区分配器。
         * TCP_NODELAY 说明：TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。
         * 启动TCP_NODELAY，就意味着禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择。
         */
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100).childOption(ChannelOption.SO_RCVBUF, 4096).childOption(ChannelOption.SO_SNDBUF, 4096)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024)).childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(buildChannelInitializer());
    }

    /**
     * 设置Initializer
     *
     * @return
     */
    protected abstract ChannelInitializer<?> buildChannelInitializer();

    @Override
    public void bind(SocketConfig config) {
        try {
            createConnectedChannel(config.getHost(), config.getPort());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected Channel createConnectedChannel(String host, int port) throws InterruptedException {
        ChannelFuture future = null;

        if (StringUtils.isEmpty(host)) {
            future = bootstrap.bind(port).sync();
        } else {
            future = bootstrap.bind(host, port).sync();
        }
        this.serverChannel = future.channel();
        return this.serverChannel;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public boolean isStarted() {
        return (this.serverChannel != null && this.serverChannel.isRegistered());
    }

    @Override
    public boolean isStopped() {
        return (this.serverChannel == null);
    }

    @Override
    public boolean isDestroyed() {
        return (this.bootstrap == null);
    }

    /**
     * 关闭服务器端的监听
     */
    @Override
    public void stop() {
        sessionManager.doClose();
        try {
            this.serverChannel.close().sync();
            this.serverChannel = null;
        } catch (InterruptedException e) {
            logger.warn("Thread interrupted closing server channel.", e);
        }
        SocketConfig connConf = sessionManager.getSocketConfig();
        logger.info("server stopped at {}:{}", connConf.getHost(), connConf.getPort());
    }

    @Override
    public void destroy() {
        stop();

        // Shut down all event loops to terminate all threads.
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        try {
            // Wait until all threads are terminated.
            bossGroup.terminationFuture().sync();
            workerGroup.terminationFuture().sync();
        } catch (InterruptedException e) {
            logger.warn("Thread interrupted closing executors.", e);
        }

        this.bootstrap = null;

        logger.info("***** destroyed netty server *****");
    }

}
