package com.drondea.sms.message.smgp30.msg;

/**
 * @version V3.0
 * @description: smgp连接请求回应包
 * @author: ywj
 * @date: 2020年06月08日10:41
 **/
public class SmgpConnectResponseMessage extends AbstractSmgpMessage {

    /**
     * 请求登录返回状态，4字节
     */
    private long status = 0;
    /**
     * 服务器端返回给客户端的认证码,16字节
     * authenticatorServer = MD5（Status+AuthenticatorClient + Shared secret）
     */
    private byte[] authenticatorServer;
    /**
     * 服务器端支持的最高版本号,1字节
     */
    private short serverVersion = 0x30;

    public SmgpConnectResponseMessage() {
        super(SmgpPackageType.CONNECTRESPONSE);
    }

    public SmgpConnectResponseMessage(SmgpHeader header) {
        super(SmgpPackageType.CONNECTRESPONSE, header);
    }

    @Override
    public int getBodyLength() {
        return 21;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public byte[] getAuthenticatorServer() {
        return authenticatorServer;
    }

    public void setAuthenticatorServer(byte[] authenticatorServer) {
        this.authenticatorServer = authenticatorServer;
    }

    public short getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(short serverVersion) {
        this.serverVersion = serverVersion;
        getHeader().setBodyLength(getBodyLength());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SmgpConnectRequestMessage:[SequenceId=").append(
                getHeader().getSequenceId()).append(",");
        buffer.append("CommandId=").append(getHeader().getCommandId()).append(",");
        buffer.append("status=").append(status).append(",");
        buffer.append("serverVersion=").append(serverVersion).append(",");
        buffer.append("authenticatorServer=").append(authenticatorServer).append("]");
        return buffer.toString();
    }
}
