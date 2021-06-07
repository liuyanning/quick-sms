package com.drondea.sms.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * smgp SequenceNumber生成 取值范围 000000～999999
 *
 * @author ywj
 */
public class SmgpSequenceNumber implements SequenceNumber{

    public static final AtomicInteger DEFAULT_VALUE = new AtomicInteger(0);
    public static final int MAX_VALUE = 999999;

    private AtomicInteger value;

    public SmgpSequenceNumber() {
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

