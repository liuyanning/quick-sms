package com.drondea.sms.type;

import org.apache.commons.lang3.StringUtils;

/**
 * 签名方向
 *
 * @author volcano
 * @version V1.0
 * @date 2019年9月26日下午3:31:50
 */
public enum SignatureDirection {
    /**
     * 自定义
     */
    CUSTOM,
    /**
     * 通道侧（固签）
     */
    CHANNEL_FIXED,
    /**
     * 混合（既有自定义也有固签）
     */
    MIXED;

    private String value;

    private SignatureDirection() {
        this.value = this.name().toLowerCase();
    }

    private SignatureDirection(String value) {
        this.value = value.toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return this.value.equalsIgnoreCase(value);
    }
}
