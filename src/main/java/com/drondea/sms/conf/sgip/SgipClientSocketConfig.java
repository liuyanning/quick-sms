package com.drondea.sms.conf.sgip;

import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.type.SgipConstants;

import java.nio.charset.Charset;

/**
 * @version V3.0.0
 * @description: Sgip的客户端配置
 * @author: liyuehai
 * @date: 2020年06月05日10:52
 **/
public class SgipClientSocketConfig extends ClientSocketConfig {

    /**
     * 联通sequenceId中的前4个字节，节点编号
     */
    private long nodeId = 0;

    private String groupName = "";

    private short loginType;
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
     *
     */
    private Charset charset = SgipConstants.DEFAULT_TRANSPORT_CHARSET;


    public SgipClientSocketConfig(String id, long bindTimeout, int windowSize, String host, int port) {
        super(id, bindTimeout, windowSize, host, port);
    }

    public SgipClientSocketConfig(String id, long bindTimeout, int windowSize, String host, int port, String groupName, String userName, String password,short loginType) {
        super(id, bindTimeout, windowSize, host, port);
        this.groupName = groupName;
        this.userName = userName;
        this.password = password;
        this.loginType = loginType;
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

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public short getLoginType() {
        return loginType;
    }

    public void setLoginType(short loginType) {
        this.loginType = loginType;
    }
}
