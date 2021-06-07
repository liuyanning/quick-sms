package com.drondea.sms.conf.smpp;

import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.SmppConstants;

/**
 * @version V3.0.0
 * @description: smpp的服务器端配置
 * @author: gengjinbiao
 * @date: 2020年07月15日17:50
 **/
public class SmppServerSocketConfig extends ServerSocketConfig {

    /**
     * 默认为3.4协议
     */
    private short version = SmppConstants.VERSION_3_4;

    public SmppServerSocketConfig(String id, int port) {
        super(id, port);
    }

    public SmppServerSocketConfig(String id, String host, int port) {
        super(id, host, port);
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }
}
