package com.drondea.sms.conf.smgp;

import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.type.SmgpConstants;

/**
 * @version V3.0
 * @description: smgp的服务器端配置
 * @author: ywj
 * @date: 2020年06月18日17:50
 **/
public class SmgpServerSocketConfig extends ServerSocketConfig {

    /**
     * smgp 3.0协议
     */
    private short version = SmgpConstants.DEFAULT_VERSION;

    public SmgpServerSocketConfig(String id, int port) {
        super(id, port);
    }

    public SmgpServerSocketConfig(String id, String host, int port) {
        super(id, host, port);
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }
}
