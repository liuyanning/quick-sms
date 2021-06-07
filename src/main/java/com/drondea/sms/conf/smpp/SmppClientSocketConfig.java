package com.drondea.sms.conf.smpp;

import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.SmppConstants;

import java.nio.charset.Charset;

/**
 * @version V3.0.0
 * @description: Smpp的客户端配置
 * @author: gengjinbiao
 * @date: 2020年07月10日10:52
 **/
public class SmppClientSocketConfig extends ClientSocketConfig {

    private String groupName = "";

    private String systemId;
    private String password;
    private String systemType;


    /**
     * 默认为3.4协议
     */
    private short version = SmppConstants.VERSION_3_4;
    private Charset charset = CmppConstants.DEFAULT_TRANSPORT_CHARSET;


    public SmppClientSocketConfig(String id, long bindTimeout, int windowSize, String host, int port) {
        super(id, bindTimeout, windowSize, host, port);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }
}
