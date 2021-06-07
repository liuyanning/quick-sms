package com.drondea.sms.type;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.handler.UserEventHandler;
import com.drondea.sms.handler.sgip.*;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @version V3.0.0
 * @description: cmpp的变量
 * @author: 刘彦宁
 * @date: 2020年06月05日10:59
 **/
public class SgipConstants {

    public static final Charset DEFAULT_TRANSPORT_CHARSET = StandardCharsets.US_ASCII;

    public final static SmsDcs DEFAULT_MSG_FMT = SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.ASCII);

    public static final SgipIdleStateHandler IDLE_STATE_HANDLER = new SgipIdleStateHandler();

    public static final AttributeKey<ChannelSession> NETTY_SESSION_KEY = AttributeKey.valueOf("netty.session");

    public final static SgipServerSessionFilterHandler SERVER_SESSION_FILTER_HANDLER = new SgipServerSessionFilterHandler();
    public final static SgipUnbindRequestMessageHandler UNBIND_REQUEST_MESSAGE_HANDLER = new SgipUnbindRequestMessageHandler();
    public final static SgipUnbindResponseMessageHandler UNBIND_RESPONSE_MESSAGE_HANDLER = new SgipUnbindResponseMessageHandler();
    public final static SgipSubmitLongMessageHandler SUBMIT_LONG_MESSAGE_HANDLER = new SgipSubmitLongMessageHandler();
    public final static SgipDeliverLongMessageHandler DELIVER_LONG_MESSAGE_HANDLER = new SgipDeliverLongMessageHandler();
    public final static UserEventHandler SGIP_USER_EVENT_HANDLER = new UserEventHandler();

    /**
     * 两种限速模式，1只是计数，2控制读取速度
     */
    public final static int DEFAULT_SERVER_RATELIMITER_TYPE = 2;
}
