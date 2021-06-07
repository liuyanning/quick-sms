package com.drondea.sms.message.smpp34;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.message.smpp34.codec.SmppAlertNotificationMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppBindTransceiverRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppBindTransceiverResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppBroadcastSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppBroadcastSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppCancelBroadcastSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppCancelBroadcastSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppCancelSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppCancelSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppDataSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppDataSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppDeliverSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppDeliverSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppEnquireLinkRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppEnquireLinkResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppGenericNackMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppOutBindMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppQueryBroadcastSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppQueryBroadcastSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppQuerySmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppQuerySmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppReplaceSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppReplaceSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppSubmitMultiRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppSubmitMultiResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppSubmitSmRequestMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppSubmitSmResponseMessageCodec;
import com.drondea.sms.message.smpp34.codec.SmppUnBindResponseMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V3.0.0
 * @description: smpp3.0的各种命令类型
 * @author: gengjinbiao
 * @date: 2020年07月14日10:58
 **/
public enum SmppPackageType implements IPackageType {

    QUERYSMREQUEST(0x00000003, SmppQuerySmRequestMessageCodec.class),
    QUERYSMRESPONSE(0x80000003, SmppQuerySmResponseMessageCodec.class),

    SUBMITSMREQUEST(0x00000004, SmppSubmitSmRequestMessageCodec.class),
    SUBMITSMRESPONSE(0x80000004, SmppSubmitSmResponseMessageCodec.class),

    DELIVERSMREQUEST(0x00000005, SmppDeliverSmRequestMessageCodec.class),
    DELIVERSMRESPONSE(0x80000005, SmppDeliverSmResponseMessageCodec.class),

    UNBINDREQUEST(0x00000006, SmppUnBindResponseMessageCodec.class),
    UNBINDRESPONSE(0x80000006, SmppUnBindResponseMessageCodec.class),

    REPLACESMREQUEST(0x00000007, SmppReplaceSmRequestMessageCodec.class),
    REPLACESMRESPONSE(0x80000007, SmppReplaceSmResponseMessageCodec.class),

    CANCELSMREQUEST(0x00000008, SmppCancelSmRequestMessageCodec.class),
    CANCELSMRESPONSE(0x80000008, SmppCancelSmResponseMessageCodec.class),

    /**
     * 连接请求
     */
    BINDTRANSCEIVERREQUEST(0x00000009, SmppBindTransceiverRequestMessageCodec.class),
    BINDTRANSCEIVERRESPONSE(0x80000009, SmppBindTransceiverResponseMessageCodec.class),

    ENQUIRELINKREQUEST(0x00000015, SmppEnquireLinkRequestMessageCodec.class),
    ENQUIRELINKRESPONSE(0x80000015, SmppEnquireLinkResponseMessageCodec.class),

    SUBMITMULTIREQUEST(0x00000021, SmppSubmitMultiRequestMessageCodec.class),
    SUBMITMULTIRESPONSE(0x80000021, SmppSubmitMultiResponseMessageCodec.class),

    DATASMREQUEST(0x00000103, SmppDataSmRequestMessageCodec.class),
    DATASMRESPONSE(0x80000103, SmppDataSmResponseMessageCodec.class),

    BROADCASTSMREQUEST(0x00000111, SmppBroadcastSmRequestMessageCodec.class),
    BROADCASTSMRESPONSE(0x80000111, SmppBroadcastSmResponseMessageCodec.class),

    QUERYBROADCASTSMREQUEST(0x00000112, SmppQueryBroadcastSmRequestMessageCodec.class),
    QUERYBROADCASTSMRESPONSE(0x80000112, SmppQueryBroadcastSmResponseMessageCodec.class),

    CANCELBROADCASTSMREQUEST(0x00000113, SmppCancelBroadcastSmRequestMessageCodec.class),
    CANCELBROADCASTSMRESPONSE(0x80000113, SmppCancelBroadcastSmResponseMessageCodec.class),

    OUTBIND(0x0000000B, SmppOutBindMessageCodec.class),

    GENERICNACK(0x80000000, SmppGenericNackMessageCodec.class),

    ALERTNOTIFICATION(0x00000102, SmppAlertNotificationMessageCodec.class);


    private static final Logger logger = LoggerFactory.getLogger(SmppPackageType.class);

    private int commandId;
    private Class<? extends ICodec> codec;
    private int bodyLength;

    private SmppPackageType(int commandId, Class<? extends ICodec> codec) {
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
