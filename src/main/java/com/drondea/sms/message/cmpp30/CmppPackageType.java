package com.drondea.sms.message.cmpp30;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.message.cmpp.codec.*;
import com.drondea.sms.message.cmpp30.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: cmpp3.0的各种命令类型
 * @author: 刘彦宁
 * @date: 2020年06月08日10:58
 **/
public enum CmppPackageType implements IPackageType {
    /**
     * 连接请求
     */
    CONNECTREQUEST(0x00000001, CmppConnectRequestMessageCodec.class),
    /**
     * 连接相应
     */
    CONNECTRESPONSE(0x80000001, CmppConnectResponseMessageCodec.class),

    /**
     * 提交短信
     */
    SUBMITREQUEST(0x00000004, CmppSubmitRequestMessageCodec.class),
    /**
     * 提交短信响应
     */
    SUBMITRESPONSE(0x80000004, CmppSubmitResponseMessageCodec.class),
    /**
     * 回执请求
     */
    DELIVERREQUEST(0x00000005, CmppDeliverRequestMessageCodec.class),
    /**
     * 回执响应
     */
    DELIVERRESPONSE(0x80000005, CmppDeliverResponseMessageCodec.class),
    /**
     * 心跳检测
     */
    ACTIVETESTREQUEST(0x00000008, CmppActiveTestRequestMessageCodec.class),
    /**
     * 心跳检测响应
     */
    ACTIVETESTRESPONSE(0x80000008, CmppActiveTestResponseMessageCodec.class),

    /**
     * 心跳检测
     */
    TERMINATEREQUEST(0x00000002, CmppTerminateRequestMessageCodec.class),
    /**
     * 心跳检测响应
     */
    TERMINATERESPONSE(0x80000002, CmppTerminateResponseMessageCodec.class);

    private static final Logger logger = LoggerFactory.getLogger(CmppPackageType.class);

    private int commandId;
    private Class<? extends ICodec> codec;
    private int bodyLength;

    private CmppPackageType(int commandId, Class<? extends ICodec> codec) {
        this.commandId = commandId;
        this.codec = codec;
    }

    @Override
    public int getCommandId() {
        return commandId;
    }

    @Override
    public ICodec getCodec() {
        try {
            return codec.newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
