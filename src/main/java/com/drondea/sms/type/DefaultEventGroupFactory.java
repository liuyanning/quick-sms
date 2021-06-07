package com.drondea.sms.type;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @version V3.0.0
 * @description: 默认的eventGroup实现
 * @author: 刘彦宁
 * @date: 2020年06月08日09:02
 **/
public class DefaultEventGroupFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventGroupFactory.class);

    private static class DefaultEventGroupFactoryHolder {
        private final static DefaultEventGroupFactory INSTANCE = new DefaultEventGroupFactory();
    }

    private DefaultEventGroupFactory() {
    }

    public static DefaultEventGroupFactory getInstance() {
        return DefaultEventGroupFactoryHolder.INSTANCE;
    }

    /**
     * cpu核心数
     */
    private int processorNum = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池异常显示处理，线程池可能吞掉异常不显示
     */
    private final ThreadUncaughtExceptionHandler EXCEPTION_HANDLER = new ThreadUncaughtExceptionHandler();

    private final CustomRejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new CustomRejectedExecutionHandler();

    private final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1,
            new BasicThreadFactory.Builder().namingPattern("bossGroup-%d").
                    uncaughtExceptionHandler(EXCEPTION_HANDLER).build());
    /**
     * nThreads默认线程数cpu*2
     */
    private final EventLoopGroup WORK_GROUP = new NioEventLoopGroup(0,
            new BasicThreadFactory.Builder().namingPattern("workerGroup-%d").
                    uncaughtExceptionHandler(EXCEPTION_HANDLER).build());

    private final ScheduledExecutorService SCHEDULE_EXECUTOR = new ScheduledThreadPoolExecutor(processorNum * 4,
            new BasicThreadFactory.Builder().namingPattern("scheduleExecutor-%d").
                    uncaughtExceptionHandler(EXCEPTION_HANDLER).build(), REJECTED_EXECUTION_HANDLER);

    private final ScheduledExecutorService COMPLETE_EXECUTOR = new ScheduledThreadPoolExecutor(processorNum * 4,
            new BasicThreadFactory.Builder().namingPattern("completeExecutor-%d").
                    uncaughtExceptionHandler(EXCEPTION_HANDLER).build(), REJECTED_EXECUTION_HANDLER);

    private final ScheduledExecutorService PULL_SCHEDULE_EXECUTOR = new ScheduledThreadPoolExecutor(
            processorNum * 4,
            new BasicThreadFactory.Builder().namingPattern("PullScheduleExecutor-%d").
                    uncaughtExceptionHandler(EXCEPTION_HANDLER).build(), REJECTED_EXECUTION_HANDLER);

    public EventLoopGroup getBoss() {
        return BOSS_GROUP;
    }

    public EventLoopGroup getWorker() {
        return WORK_GROUP;
    }

    public ScheduledExecutorService getScheduleExecutor() {
        return SCHEDULE_EXECUTOR;
    }

    public ScheduledExecutorService getCompleteExecutor() {
        return COMPLETE_EXECUTOR;
    }

    public ScheduledExecutorService getPullScheduleExecutor() {
        return PULL_SCHEDULE_EXECUTOR;
    }

    class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.error("ThreadPool {} got exception {}", thread, throwable);
        }
    }

    public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            String msg = String.format("Thread pool is EXHAUSTED!" +
                            " Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                            " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                    executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(),
                    executor.getTaskCount(), executor.getCompletedTaskCount(), executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
            // 线程池拒绝，异常处理
            logger.error(msg + ":" + executor.getQueue().size());
        }
    }

    public void close() {
        PULL_SCHEDULE_EXECUTOR.shutdown();
        WORK_GROUP.shutdownGracefully();
        BOSS_GROUP.shutdownGracefully();
        SCHEDULE_EXECUTOR.shutdown();
    }

}
