package com.drondea.sms.conf.cmpp;

import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.type.CmppConstants;

/**
 * @version V3.0.0
 * @description: cmpp的服务器端配置
 * @author: 刘彦宁
 * @date: 2020年06月18日17:50
 **/
public class CmppServerSocketConfig extends ServerSocketConfig {

    /**
     * 默认为2.0协议
     */
    private short version = CmppConstants.VERSION_20;

    public CmppServerSocketConfig(String id, int port) {
        super(id, port);
    }

    public CmppServerSocketConfig(String id, String host, int port) {
        super(id, host, port);
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }
}
