package com.drondea.sms.message.smgp30.msg;

/**
 * @version V3.0
 * @description: smgp连接请求包
 * @author: ywj
 * @date: 2020年06月08日10:34
 **/
public class SmgpConnectRequestMessage extends AbstractSmgpMessage {

    /**
     * 客户端用来登录服务器端的用户账号，8字节
     */
    private String clientId = "";
    /**
     * 鉴权字段，16字节
     */
    private byte[] authenticatorClient;
    /**
     * 登录类型，1字节:0＝发送短消息; 1＝接收短消息; 2＝收发短消息;
     */
    private byte loginMode = 2;
    /**
     * 时间戳，4字节
     */
    private long timestamp = 0L;
    /**
     * 版本号，1字节
     */
    private short clientVersion = 0x30;

    public SmgpConnectRequestMessage() {
        super(SmgpPackageType.CONNECTREQUEST);
    }

    public SmgpConnectRequestMessage(SmgpHeader header) {
        super(SmgpPackageType.CONNECTREQUEST, header);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public byte[] getAuthenticatorClient() {
        return authenticatorClient;
    }

    public void setAuthenticatorClient(byte[] authenticatorClient) {
        this.authenticatorClient = authenticatorClient;
    }

    public byte getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(byte loginMode) {
        this.loginMode = loginMode;
    }

    public short getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(short clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int getBodyLength() {
        return 30;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpConnectRequestMessage:[SequenceId=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("CommandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("clientId=").append(clientId).append(",");
        buffer.append("loginMode=").append(loginMode).append(",");
        buffer.append("timestamp=").append(timestamp).append(",");
        buffer.append("clientVersion=").append(clientVersion).append(",");
        buffer.append("authenticatorClient=").append(authenticatorClient).append("]");
        return buffer.toString();
    }

}
