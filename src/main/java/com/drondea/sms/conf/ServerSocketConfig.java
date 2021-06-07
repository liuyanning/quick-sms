package com.drondea.sms.conf;

/**
 * @version V3.0.0
 * @description: 服务器端连接配置
 * @author: 刘彦宁
 * @date: 2020年06月10日11:28
 **/
public class ServerSocketConfig extends SocketConfig {

    /**
     * 唯一ID
     */
    private String id;

    /**
     * 心跳超时检测时间，秒
     */
    private int idleTime;

    public ServerSocketConfig(String id, int port) {
        this(id, null, port);
    }

    public ServerSocketConfig(String id, String host, int port) {
        super(host, port);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }
}
