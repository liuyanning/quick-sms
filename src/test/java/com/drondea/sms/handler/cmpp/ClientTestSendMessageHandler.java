package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.DefaultEventGroupFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @version V3.0.0
 * @description: 客户端发送短信测试
 * @author: 刘彦宁
 * @date: 2020年06月17日20:17
 **/
public class ClientTestSendMessageHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientTestSendMessageHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == (Integer) ChannelSession.STATE_LOGIN_SUCCESS) {
            logger.info("用户登录成功5秒后发送短信");

            DefaultEventGroupFactory.getInstance().getScheduleExecutor().schedule(() -> {
                ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
                CmppSubmitRequestMessage requestMessage = new CmppSubmitRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceId(sequenceNumber.next());
                requestMessage.setMsgContent("您的验证码是2010【庄点科技】");
                requestMessage.setServiceId("1");
                requestMessage.setMsgSrc("002");
                requestMessage.setRegisteredDelivery((short) 1);
                requestMessage.setDestUsrTl((short) 1);
                requestMessage.setDestTerminalId(new String[]{"17332958317"});
                requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                channelSession.sendMessage(requestMessage);
//                ctx.channel().writeAndFlush(requestMessage);
            }, 5, TimeUnit.SECONDS);

        }
        super.userEventTriggered(ctx, evt);
    }
}
