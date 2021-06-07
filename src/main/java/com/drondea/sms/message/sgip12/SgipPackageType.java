package com.drondea.sms.message.sgip12;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.message.sgip12.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: sgip1.2的各种命令类型
 * @author: liyuehai
 * @date: 2020年07月07日15:23
 **/
public enum SgipPackageType implements IPackageType {
    /**
     * 连接请求
     */
    BINDREQUEST(0x1, SgipBindRequestMessageCodec.class),
    /**
     * 连接响应
     */
    BINDRESPONSE(0x80000001, SgipBindResponseMessageCodec.class),
    /**
     * 断开连接请求
     */
    UNBINDREQUEST(0x2, SgipUnbindRequestMessageCodec.class),
    /**
     * 断开连接响应
     */
    UNBINDRESPONSE(0x80000002, SgipUnbindResponseMessageCodec.class),

    /**
     * 提交短信
     */
    SUBMITREQUEST(0x3, SgipSubmitRequestMessageCodec.class),
    /**
     * 提交短信响应
     */
    SUBMITRESPONSE(0x80000003, SgipSubmitResponseMessageCodec.class),
    /**
     * 回执请求
     */
    DELIVERREQUEST(0x4, SgipDeliverRequestMessageCodec.class),
    /**
     * 回执响应
     */
    DELIVERRESPONSE(0x80000004, SgipDeliverResponseMessageCodec.class),

    /**
     * 回执请求
     */
    REPORTREQUEST(0x5, SgipReportRequestMessageCodec.class),
    /**
     * 回执响应
     */
    REPORTRESPONSE(0x80000005, SgipReportResponseMessageCodec.class);

    private static final Logger logger = LoggerFactory.getLogger(SgipPackageType.class);

    private int commandId;
    private Class<? extends ICodec> codec;

    private SgipPackageType(int commandId, Class<? extends ICodec> codec) {
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
