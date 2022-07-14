package com.drondea.sms.type;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.handler.smgp.SmgpActiveTestRequestMessageHandler;
import com.drondea.sms.handler.smgp.SmgpActiveTestResponseMessageHandler;
import com.drondea.sms.handler.smgp.SmgpDeliverLongMessageHandler;
import com.drondea.sms.handler.smgp.SmgpIdleStateHandler;
import com.drondea.sms.handler.smgp.SmgpServerSessionFilterHandler;
import com.drondea.sms.handler.smgp.SmgpSubmitLongMessageHandler;
import com.drondea.sms.handler.smgp.SmgpExitRequestMessageHandler;
import com.drondea.sms.handler.smgp.SmgpTerminateResponseMessageHandler;
import com.drondea.sms.handler.smgp.SmgpUserEventHandler;

import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;

import java.nio.charset.Charset;

import io.netty.util.AttributeKey;

/**
 * @version V3.0
 * @description: smgp的变量
 * @author: ywj
 * @date: 2020年06月05日10:59
 **/
public class SmgpConstants {

    public static final short OPT_TP_PID = 0x0001;
    public static final short OPT_TP_UDHI = 0x0002;
    public static final short OPT_LINK_ID = 0x0003;
    public static final short OPT_CHARGE_USER_TYPE = 0x0004;
    public static final short OPT_CHARGE_TERM_TYPE = 0x0005;
    public static final short OPT_CHARGE_TERM_PSEUDO = 0x0006;
    public static final short OPT_DEST_TERM_TYPE = 0x0007;
    public static final short OPT_DEST_TERM_PSEUDO = 0x0008;
    public static final short OPT_PK_TOTAL = 0x0009;
    public static final short OPT_PK_NUMBER = 0x000a;
    public static final short OPT_SUBMIT_MSG_TYPE = 0x000b;
    public static final short OPT_SP_DEAL_RESULT = 0x000c;
    public static final short OPT_SRC_TERM_TYPE = 0x000d;
    public static final short OPT_SRC_TERM_PSEUDO = 0x000e;
    public static final short OPT_NODES_COUNT = 0x000f;
    public static final short OPT_MSG_SRC = 0x0010;
    public static final short OPT_SRC_TYPE = 0x0011;
    public static final short OPT_M_SERVICE_ID = 0x0012;

    public static final Charset DEFAULT_TRANSPORT_CHARSET = Charset.forName("GBK");

    public static final short DEFAULT_VERSION = 0x30;
    public static final short VERSION_30 = 0x30;
    public static final short VERSION_13 = 0x13;
    public final static SmsDcs DEFAULT_MSG_FMT = SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.ASCII);

    public static final SmgpIdleStateHandler IDLE_STATE_HANDLER = new SmgpIdleStateHandler();

    public static final AttributeKey<ChannelSession> NETTY_SESSION_KEY = AttributeKey.valueOf("netty.session");

    public final static SmgpActiveTestRequestMessageHandler ACTIVE_TEST_REQUEST_HANDLER = new SmgpActiveTestRequestMessageHandler();
    public final static SmgpActiveTestResponseMessageHandler ACTIVE_TEST_RESPONSE_HANDLER = new SmgpActiveTestResponseMessageHandler();
    public final static SmgpServerSessionFilterHandler SERVER_SESSION_FILTER_HANDLER = new SmgpServerSessionFilterHandler();
    public final static SmgpExitRequestMessageHandler EXIT_REQUEST_MESSAGE_HANDLER = new SmgpExitRequestMessageHandler();
    public final static SmgpTerminateResponseMessageHandler TERMINATE_RESPONSE_MESSAGE_HANDLER = new SmgpTerminateResponseMessageHandler();
    public final static SmgpSubmitLongMessageHandler SUBMIT_LONG_MESSAGE_HANDLER = new SmgpSubmitLongMessageHandler();
    public final static SmgpDeliverLongMessageHandler DELIVER_LONG_MESSAGE_HANDLER = new SmgpDeliverLongMessageHandler();
    public final static SmgpUserEventHandler SMGP_USER_EVENT_HANDLER = new SmgpUserEventHandler();
}
