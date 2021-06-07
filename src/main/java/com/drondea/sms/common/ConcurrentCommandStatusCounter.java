package com.drondea.sms.common;


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发ConcurrentHashMap计数器
 */
public class ConcurrentCommandStatusCounter {

    private ConcurrentHashMap<Integer, AtomicInteger> map;

    public ConcurrentCommandStatusCounter() {
        this.map = new ConcurrentHashMap<Integer, AtomicInteger>();
    }

    public void reset() {
        this.map.clear();
    }

    public ConcurrentCommandStatusCounter copy() {
        ConcurrentCommandStatusCounter copy = new ConcurrentCommandStatusCounter();
        for (Map.Entry<Integer, AtomicInteger> entry : this.map.entrySet()) {
            this.map.put(entry.getKey(), new AtomicInteger(entry.getValue().get()));
        }
        return copy;
    }

    public int get(int commandStatus) {
        Integer key = new Integer(commandStatus);
        AtomicInteger val = map.get(key);
        if (val == null) {
            return -1;
        } else {
            return val.get();
        }
    }

    public int incrementAndGet(int commandStatus) {
        Integer key = new Integer(commandStatus);
        AtomicInteger val = map.get(key);
        if (val == null) {
            val = new AtomicInteger(0);
            map.put(key, val);
        }
        return val.incrementAndGet();
    }

    public SortedMap<Integer, Integer> createSortedMapSnapshot() {
        SortedMap<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>();
        for (Map.Entry<Integer, AtomicInteger> entry : this.map.entrySet()) {
            sortedMap.put(entry.getKey(), new Integer(entry.getValue().get()));
        }
        return sortedMap;
    }

    @Override
    public String toString() {
        if (this.map.isEmpty()) {
            return "";
        }

        SortedMap<Integer, Integer> sortedMap = createSortedMapSnapshot();

        StringBuilder to = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
            if (to.length() != 0) {
                to.append(" ");
            }
            to.append(entry.getKey());
            to.append("=");
            to.append(entry.getValue());
        }

        return to.toString();
    }

}
