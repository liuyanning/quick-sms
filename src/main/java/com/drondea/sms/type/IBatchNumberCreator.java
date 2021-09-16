package com.drondea.sms.type;

/**
 * 批次号生成器
 * @author liuyanning
 */
public interface IBatchNumberCreator {
    /**
     * 为短信生成批次号
     * @return
     */
    String generateBatchNumber();
}
