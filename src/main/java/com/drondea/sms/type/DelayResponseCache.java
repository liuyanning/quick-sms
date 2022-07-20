package com.drondea.sms.type;


import com.codahale.metrics.Timer;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.windowing.ChannelWindowMessage;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: 延迟响应信息缓存处理
 * @author: 刘彦宁
 * @date: 2020年12月10日13:58
 **/
public class DelayResponseCache {

    /**
     * 缓存12分钟，15000条
     */
    private static final Cache<String, ChannelWindowMessage> MESSAGE_CACHE = Caffeine.newBuilder().maximumSize(15_000).
            expireAfterWrite(12, TimeUnit.MINUTES).build();

    /**
     * 类加载就启动定时任务
     */
    static {
        startCleanExpiredResponseTask();
    }

    public static void putDelayMessage(String key, ChannelWindowMessage message) {
        MESSAGE_CACHE.put(key, message);
    }

    public static long getCacheSize() {
        return MESSAGE_CACHE.estimatedSize();
    }

    public static ChannelWindowMessage getAndRemoveDelayMessage(String key) {
        return MESSAGE_CACHE.asMap().remove(key);
    }

    public static void startCleanExpiredResponseTask() {
        DefaultEventGroupFactory.getInstance().getScheduleExecutor().scheduleWithFixedDelay(()->{
            try {
                cleanTask();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private static void cleanTask(){
        ConcurrentMap<String, ChannelWindowMessage> map = MESSAGE_CACHE.asMap();
        Iterator<Map.Entry<String, ChannelWindowMessage>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ChannelWindowMessage> next = iterator.next();
            ChannelWindowMessage windowMessage = next.getValue();
            String key = next.getKey();

            IMessage msg = windowMessage.getMessage();
            if (msg == null) {
                continue;
            }

            //发送时间
            long sendTime = msg.getSendTimeStamp();
            long now = System.currentTimeMillis();
            //超过10分钟的, 过期
            if (sendTime > 0 && now - sendTime > 10 * 60 * 1000) {
                //移除过期消息触发过期
                ChannelWindowMessage removeMsg = map.remove(key);
                handleMessageExpired(key, removeMsg);
            }
        }
    }


    private static void handleMessageExpired(String key, ChannelWindowMessage windowMessage) {

        if (windowMessage == null) {
            return;
        }

        //统计窗口发送速率用
        Timer.Context timeContext = windowMessage.getTimeContext();
        if (timeContext != null) {
            timeContext.stop();
        }

        IMessage message = windowMessage.getMessage();
        if (message != null) {
            message.handleMessageExpired(key, message);
        }

    }
}
