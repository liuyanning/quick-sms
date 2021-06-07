package com.drondea.sms.conf.sgip;

import com.drondea.sms.conf.ServerSocketConfig;

/**
 * @version V3.0.0
 * @description: sgip的服务器端配置
 * @author: liyuehai
 * @date: 2020年06月18日17:50
 **/
public class SgipServerSocketConfig extends ServerSocketConfig {

    public SgipServerSocketConfig(String id, int port) {
        super(id, port);
    }

    public SgipServerSocketConfig(String id, String host, int port) {
        super(id, host, port);
    }

}
