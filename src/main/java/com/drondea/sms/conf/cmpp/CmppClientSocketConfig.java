package com.drondea.sms.conf.cmpp;

import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.type.CmppConstants;

import java.nio.charset.Charset;

/**
 * @version V3.0.0
 * @description: cmpp的客户端配置
 * @author: 刘彦宁
 * @date: 2020年06月05日10:52
 **/
public class CmppClientSocketConfig extends ClientSocketConfig {

    private String groupName = "";

    private String userName;
    private String password;


    private String spCode = "";
    /**
     * 服务代码
     */
    private String serviceId = "";
    /**
     * 企业代码，可能跟userName相同
     */
    private String msgSrc = "";
    /**
     * 默认为2.0协议
     */
    private short version = CmppConstants.VERSION_20;
    private Charset charset = CmppConstants.DEFAULT_TRANSPORT_CHARSET;


    public CmppClientSocketConfig(String id, long bindTimeout, int windowSize, String host, int port) {
        super(id, bindTimeout, windowSize, host, port);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMsgSrc() {
        return msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
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
}
