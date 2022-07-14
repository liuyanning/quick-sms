package com.drondea.sms.type;


/**
 * @version V3.0.0
 * @description: 用户信息实体类
 * @author: 刘彦宁
 * @date: 2020年06月10日17:58
 **/
public class UserChannelConfig {

    private String id;
    private String userName;
    private String password;
    private String validIp;
    private int qpsLimit;
    private int channelLimit;
    private int idleTime;

    /**
     * 开启滑动窗口发送回执和上行短信
     */
    private int windowSize = 16;
    private int windowMonitorInterval = 30 * 1000;
    /**
     * 请求响应超时时间（毫秒）
     */
    private long requestExpiryTimeout = 30 * 1000;

//    private Charset charset;
    private short version;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidIp() {
        return validIp;
    }

    public void setValidIp(String validIp) {
        this.validIp = validIp;
    }

    public int getQpsLimit() {
        return qpsLimit;
    }

    public void setQpsLimit(int qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

//    public Charset getCharset() {
//        return charset;
//    }
//
//    public void setCharset(Charset charset) {
//        this.charset = charset;
//    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }


    public int getChannelLimit() {
        return channelLimit;
    }

    public void setChannelLimit(int channelLimit) {
        this.channelLimit = channelLimit;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getWindowMonitorInterval() {
        return windowMonitorInterval;
    }

    public void setWindowMonitorInterval(int windowMonitorInterval) {
        this.windowMonitorInterval = windowMonitorInterval;
    }

    public long getRequestExpiryTimeout() {
        return requestExpiryTimeout;
    }

    public void setRequestExpiryTimeout(long requestExpiryTimeout) {
        this.requestExpiryTimeout = requestExpiryTimeout;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }
}
