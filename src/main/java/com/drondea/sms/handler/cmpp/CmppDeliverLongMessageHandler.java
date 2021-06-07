package com.drondea.sms.handler.cmpp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.cmpp.CmppDeliverRequestMessage;
import com.drondea.sms.message.cmpp.CmppSubmitResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * cmpp的上行短信的处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author liuyanning
 */
@Sharable
public class CmppDeliverLongMessageHandler extends AbstractLongMessageHandler<CmppDeliverRequestMessage> {

    @Override
    protected boolean needHandleLongMessage(CmppDeliverRequestMessage msg) {
        //状态通知的不用处理
        return !msg.isReport();
    }

    @Override
    protected IMessage responseErr(CmppDeliverRequestMessage msg) {
        CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());
        //消息结构错误
        responseMessage.setResult(1);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(CmppDeliverRequestMessage msg) {
        return StringUtils.join(msg.getSrcTerminalId(), "|") + msg.getDestId() + ".";
    }
}
