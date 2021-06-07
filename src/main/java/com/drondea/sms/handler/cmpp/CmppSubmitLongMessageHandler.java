package com.drondea.sms.handler.cmpp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppSubmitRequestMessage;
import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * cmpp的长短信处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author liuyanning
 */
@Sharable
public class CmppSubmitLongMessageHandler extends AbstractLongMessageHandler<CmppSubmitRequestMessage> {


    @Override
    protected boolean needHandleLongMessage(CmppSubmitRequestMessage msg) {
        return true;
    }

    @Override
    protected IMessage responseErr(CmppSubmitRequestMessage msg) {
        CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());
        //消息结构错误
        responseMessage.setResult(1);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(CmppSubmitRequestMessage msg) {
        return StringUtils.join(msg.getDestTerminalId(), "|") + msg.getSrcId() + ".";
    }
}
