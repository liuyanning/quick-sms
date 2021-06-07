package com.drondea.sms.conf;

import com.drondea.sms.type.GlobalConstants;

/**
 * @version V3.0.0
 * @description: 基础的连接类型
 * @author: 刘彦宁
 * @date: 2020年06月05日10:22
 **/
public class SocketConfig {

    private String id;
    private String host;
    private int port;
    private long connectTimeout;
    private boolean countersEnabled;

    public SocketConfig(String host, int port) {
        this(host, port, GlobalConstants.DEFAULT_CONNECT_TIMEOUT);
    }

    public SocketConfig(String host, int port, long connectTimeout) {
        this.host = host;
        this.port = port;
        this.connectTimeout = connectTimeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isCountersEnabled() {
        return countersEnabled;
    }

    public void setCountersEnabled(boolean countersEnabled) {
        this.countersEnabled = countersEnabled;
    }

    public String getId() {
        return id;
    }
}
