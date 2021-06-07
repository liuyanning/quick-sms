package com.drondea.sms.type;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: 使用metrics度量性能
 * @author: 刘彦宁
 * @date: 2020年07月07日15:18
 **/
public class Metrics {

    private MetricRegistry registry;

    private static class SingletonClassInstance {
        private static final Metrics metrics = new Metrics();
    }

    private Metrics() {

        this.registry = new MetricRegistry();

        final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();

        //logback要添加logback的maven依赖
//        LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
//        Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);
//        InstrumentedAppender metrics = new InstrumentedAppender(registry);
//        metrics.setContext(root.getLoggerContext());
//        metrics.start();
//        root.addAppender(metrics);

        /* 实例化ConsoleReporter，输出 */
        if (GlobalConstants.METRICS_CONSOLE_ON) {
            ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.SECONDS)
                    .build();
            //从启动后的3s后开始（所以通常第一个计数都是不准的，从第二个开始会越来越准），每隔3秒从MetricRegistry钟poll一次数据
            consoleReporter.start(3, TimeUnit.SECONDS);
        }
    }

    public static Metrics getInstance() {
        return SingletonClassInstance.metrics;
    }

    public static void remove(String name) {
        getInstance().getRegistry().remove(name);
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;
    }
}
