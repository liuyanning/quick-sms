package com.drondea.sms.message.sgip12;

/**
 * @version V3.0.0
 * @description: sgip连接请求包
 * @author: liyuehai
 * @date: 2020年07月07日18:21
 **/
public class SgipBindRequestMessage extends AbstractSgipMessage {
    /**
     * 登录类型，1字节
     */
    private short loginType = 1;
    /**
     * 登录名，16字节
     */
    private String loginName = "";
    /**
     * 密码，16字节
     */
    private String loginPassowrd = "";
    /**
     * 扩展，8字节
     */
    private String reserve = "";


    public SgipBindRequestMessage() {
        super(SgipPackageType.BINDREQUEST);
    }

    public SgipBindRequestMessage(SgipHeader header) {
        super(SgipPackageType.BINDREQUEST, header);
    }

    public short getLoginType() {
        return loginType;
    }

    public void setLoginType(short loginType) {
        this.loginType = loginType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassowrd() {
        return loginPassowrd;
    }

    public void setLoginPassowrd(String loginPassowrd) {
        this.loginPassowrd = loginPassowrd;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    @Override
    public int getBodyLength() {
        return 41;
    }
    @Override
    public String toString() {
        return "SgipBindRequestMessage [loginType="+loginType+", reserve="+ reserve
                +",header="+getHeader().toString()+"]";
    }

}
