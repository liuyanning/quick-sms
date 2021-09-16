package com.drondea.sms.type;

import com.drondea.sms.common.util.MsgId;

/**
 * 默认批次号生成器
 * @author liuyanning
 */
public class DefaultBatchNumberCreator implements IBatchNumberCreator{
    @Override
    public String generateBatchNumber() {
        return new MsgId().toString();
    }
}
