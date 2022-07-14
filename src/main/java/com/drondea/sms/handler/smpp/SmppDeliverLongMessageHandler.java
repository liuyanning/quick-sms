package com.drondea.sms.handler.smpp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smpp34.SmppDeliverSmRequestMessage;
import com.drondea.sms.message.smpp34.SmppDeliverSmResponseMessage;
import com.drondea.sms.type.SmppConstants;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * smpp的上行短信的处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author liuyanning
 */
@Sharable
public class SmppDeliverLongMessageHandler extends AbstractLongMessageHandler<SmppDeliverSmRequestMessage> {

    @Override
    protected boolean needHandleLongMessage(SmppDeliverSmRequestMessage msg) {
        //状态通知的不用处理
        return !msg.isReport();
    }

    @Override
    protected IMessage responseErr(SmppDeliverSmRequestMessage msg) {
        SmppDeliverSmResponseMessage responseMessage = new SmppDeliverSmResponseMessage(msg.getHeader());
        //消息错误
        responseMessage.getHeader().setCommandStatus(SmppConstants.STATUS_SYSERR);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SmppDeliverSmRequestMessage msg) {
        return StringUtils.join(msg.getDestinationAddr(), "|") + msg.getSourceAddr() + ".";
    }
}
