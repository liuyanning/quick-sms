package com.drondea.sms.handler.smpp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppSubmitSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppSubmitSmResponseMessage;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * smpp的长短信处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author gengjinbiao
 */
@Sharable
public class SmppSubmitLongMessageHandler extends AbstractLongMessageHandler<SmppSubmitSmRequestMessage> {


    @Override
    protected boolean needHandleLongMessage(SmppSubmitSmRequestMessage msg) {
        return true;
    }

    @Override
    protected IMessage responseErr(SmppSubmitSmRequestMessage msg) {
        SmppSubmitSmResponseMessage responseMessage = new SmppSubmitSmResponseMessage(msg.getHeader());
        responseMessage.getHeader().setCommandStatus(SmppConstants.STATUS_SYSERR);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SmppSubmitSmRequestMessage msg) {
        return StringUtils.join(msg.getDestinationAddr(), "|") + msg.getSourceAddr() + ".";
    }

}
