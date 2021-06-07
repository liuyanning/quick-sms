package com.drondea.sms.conf;

import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.LoggingOptions;
import com.drondea.sms.type.SignatureDirection;
import com.drondea.sms.type.SignaturePosition;

/**
 * @version V3.0.0
 * @description: 客户端配置
 * @author: 刘彦宁
 * @date: 2020年06月05日10:51
 **/
public class ClientSocketConfig extends SocketConfig {

    /**
     * 唯一ID
     */
    private String id;

    /**
     * 流量限制
     */
    private int qpsLimit;

    /**
     * 连接服务器超时时间，单位秒
     */
    private long bindTimeout;
    /**
     * 滑动窗口size
     */
    private int windowSize;

    /**
     * 滑动窗口超时检测间隔,建议 requestExpiryTimeout * 0.5（毫秒）
     */
    private int windowMonitorInterval;
    /**
     * 请求响应超时时间（毫秒）
     */
    private long requestExpiryTimeout;


    /**
     * 最多可以连接的数量
     */
    private int channelSize;

    private LoggingOptions loggingOptions;

    /**
     * 心跳超时时间,单位秒
     */
    private int idleTime;

    /**
     * 签名方向，自定义和通道侧签名
     */
    private SignatureDirection signatureDirection;

    /**
     * 此通道的签名字符串
     */
    private String smsSignature;

    /**
     * 此通道的签名类型，分为前置和后置
     */
    private SignaturePosition signaturePosition;


    public ClientSocketConfig(String id, long bindTimeout, int windowSize, String host, int port) {
        super(host, port);
        this.id = id;
        this.bindTimeout = bindTimeout;
        this.windowSize = windowSize;
        this.windowMonitorInterval = GlobalConstants.DEFAULT_WINDOW_MONITOR_INTERVAL;
        this.requestExpiryTimeout = GlobalConstants.DEFAULT_REQUEST_EXPIRY_TIMEOUT;
        this.signatureDirection = SignatureDirection.CUSTOM;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public long getBindTimeout() {
        return bindTimeout;
    }

    public void setBindTimeout(long bindTimeout) {
        this.bindTimeout = bindTimeout;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getChannelSize() {
        return channelSize;
    }

    public void setChannelSize(int channelSize) {
        this.channelSize = channelSize;
    }

    public LoggingOptions getLoggingOptions() {
        return loggingOptions;
    }

    public void setLoggingOptions(LoggingOptions loggingOptions) {
        this.loggingOptions = loggingOptions;
    }

    public long getRequestExpiryTimeout() {
        return requestExpiryTimeout;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public SignatureDirection getSignatureDirection() {
        return signatureDirection;
    }

    public void setSignatureDirection(SignatureDirection signatureDirection) {
        this.signatureDirection = signatureDirection;
    }

    public String getSmsSignature() {
        return smsSignature;
    }

    public void setSmsSignature(String smsSignature) {
        this.smsSignature = smsSignature;
    }

    public SignaturePosition getSignaturePosition() {
        return signaturePosition;
    }

    public void setSignaturePosition(SignaturePosition signaturePosition) {
        this.signaturePosition = signaturePosition;
    }

    public int getWindowMonitorInterval() {
        return windowMonitorInterval;
    }

    public void setWindowMonitorInterval(int windowMonitorInterval) {
        this.windowMonitorInterval = windowMonitorInterval;
    }

    public void setRequestExpiryTimeout(long requestExpiryTimeout) {
        this.requestExpiryTimeout = requestExpiryTimeout;
    }

    public int getQpsLimit() {
        return qpsLimit;
    }

    public void setQpsLimit(int qpsLimit) {
        this.qpsLimit = qpsLimit;
    }
}
