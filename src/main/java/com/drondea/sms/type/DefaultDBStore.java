package com.drondea.sms.type;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: 默认的存储，本地存储
 * @author: 刘彦宁
 * @date: 2020年06月15日10:29
 **/
public class DefaultDBStore<T extends Object> implements IDBStore<T> {

    private final Logger logger = LoggerFactory.getLogger(DefaultDBStore.class);

    private final RemovalListener<String, Object> removalListener = notification -> {
        RemovalCause cause = notification.getCause();
        Object h = notification.getValue();
        switch (cause) {
            case EXPIRED:
            case SIZE:
            case COLLECTED:
                logger.error("Default DBStore Lost cause by {}. value {}", cause,
                        h.toString());
            default:
                return;
        }
    };

    private Cache<String, T> cache = CacheBuilder.newBuilder().
            expireAfterAccess(2, TimeUnit.HOURS).removalListener(removalListener).build();
    private ConcurrentMap<String, T> map = cache.asMap();


    @Override
    public T get(String key) {
        return map.get(key);
    }


    @Override
    public T put(String key, T value) {
        return map.put(key, value);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public T putIfAbsent(String key, T value) {
        return map.putIfAbsent(key, value);
    }
}
