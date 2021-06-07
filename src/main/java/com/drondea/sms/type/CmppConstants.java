package com.drondea.sms.type;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.handler.*;
import com.drondea.sms.handler.cmpp.*;
import io.netty.util.AttributeKey;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @version V3.0.0
 * @description: cmpp的变量
 * @author: 刘彦宁
 * @date: 2020年06月05日10:59
 **/
public class CmppConstants {

    public static final Charset DEFAULT_TRANSPORT_CHARSET = StandardCharsets.US_ASCII;

    public static final short VERSION_30 = 0x30;
    public static final short VERSION_20 = 0x20;
    public final static SmsDcs DEFAULT_MSG_FMT = SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.ASCII);

    public static final CmppIdleStateHandler IDLE_STATE_HANDLER = new CmppIdleStateHandler();

    public static final AttributeKey<ChannelSession> NETTY_SESSION_KEY = AttributeKey.valueOf("netty.session");

    public final static CmppActiveTestRequestMessageHandler ACTIVE_TEST_REQUEST_HANDLER = new CmppActiveTestRequestMessageHandler();
    public final static CmppActiveTestResponseMessageHandler ACTIVE_TEST_RESPONSE_HANDLER = new CmppActiveTestResponseMessageHandler();
    public final static CmppServerSessionFilterHandler SERVER_SESSION_FILTER_HANDLER = new CmppServerSessionFilterHandler();
    public final static CmppTerminateRequestMessageHandler TERMINATE_REQUEST_MESSAGE_HANDLER = new CmppTerminateRequestMessageHandler();
    public final static CmppTerminateResponseMessageHandler TERMINATE_RESPONSE_MESSAGE_HANDLER = new CmppTerminateResponseMessageHandler();
    public final static CmppSubmitLongMessageHandler SUBMIT_LONG_MESSAGE_HANDLER = new CmppSubmitLongMessageHandler();
    public final static CmppDeliverLongMessageHandler DELIVER_LONG_MESSAGE_HANDLER = new CmppDeliverLongMessageHandler();
    public final static UserEventHandler CMPP_USER_EVENT_HANDLER = new UserEventHandler();

    /**
     * 两种限速模式，1只是计数，2控制读取速度
     */
    public final static int DEFAULT_SERVER_RATELIMITER_TYPE = 2;
}
