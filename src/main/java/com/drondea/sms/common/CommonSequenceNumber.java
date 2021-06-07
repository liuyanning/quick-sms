package com.drondea.sms.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SequenceNumber生成
 *
 * @author liuyanning
 */
public class CommonSequenceNumber implements SequenceNumber{

    public static final AtomicInteger DEFAULT_VALUE = new AtomicInteger(0);
    public static final int MAX_VALUE = 0x7FFFFFFF;

    private AtomicInteger value;

    public CommonSequenceNumber() {
        this.value = DEFAULT_VALUE;
    }

    /**
     * 获取下一个sequenceNumber
     */
    @Override
    public int next() {
        return value.updateAndGet((v) -> {
            // 同步检查
            if (v >= MAX_VALUE) {
                return 1;
            }
            return v + 1;
        });
    }

    @Override
    public int getValue() {
        return value.get();
    }

    /**
     * 重置最小值
     */
    synchronized public void reset() {
        this.value = DEFAULT_VALUE;
    }

}

