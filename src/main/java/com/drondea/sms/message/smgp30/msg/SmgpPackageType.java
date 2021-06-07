package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.message.smgp30.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0
 * @description: smgp的各种命令类型
 * @author: ywj
 * @date: 2020年06月08日10:58
 **/
public enum SmgpPackageType implements IPackageType {
    /**
     * 连接请求
     */
    CONNECTREQUEST(0x00000001, SmgpConnectRequestMessageCodec.class),
    /**
     * 连接相应
     */
    CONNECTRESPONSE(0x80000001, SmgpConnectResponseMessageCodec.class),

    /**
     * 提交短信
     */
    SUBMITREQUEST(0x00000002, SmgpSubmitRequestMessageCodec.class),
    /**
     * 提交短信响应
     */
    SUBMITRESPONSE(0x80000002, SmgpSubmitResponseMessageCodec.class),
    /**
     * 回执请求
     */
    DELIVERREQUEST(0x00000003, SmgpDeliverRequestMessageCodec.class),
    /**
     * 回执响应
     */
    DELIVERRESPONSE(0x80000003, SmgpDeliverResponseMessageCodec.class),
    /**
     * 心跳检测
     */
    ACTIVETESTREQUEST(0x00000004, SmgpActiveTestRequestMessageCodec.class),
    /**
     * 心跳检测响应
     */
    ACTIVETESTRESPONSE(0x80000004, SmgpActiveTestResponseMessageCodec.class),

    /**
     * 断开检测
     */
    TERMINATEREQUEST(0x00000006, SmgpTerminateRequestMessageCodec.class),
    /**
     * 断开检测响应
     */
    TERMINATERESPONSE(0x80000006, SmgpTerminateResponseMessageCodec.class);

    private static final Logger logger = LoggerFactory.getLogger(SmgpPackageType.class);

    private int commandId;
    private Class<? extends ICodec> codec;

    private SmgpPackageType(int commandId, Class<? extends ICodec> codec) {
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
