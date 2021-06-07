package com.drondea.sms.type;

/**
 * 数据库操作接口，此操作类似于Map对应操作
 *
 * @author liuyanning
 */
public interface IDBStore<T extends Object> {

    /**
     * 根据key获取对应的value
     *
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 根据key存入value
     *
     * @param key
     * @param value
     * @return
     */
    T put(String key, T value);


    /**
     * 删除key
     *
     * @param key
     */
    void remove(String key);


    /**
     * 一定保证线程安全
     *
     * @param key
     */
    T putIfAbsent(String key, T value);
}
