package com.drondea.sms.handler.sgip;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipDeliverRequestMessage;
import com.drondea.sms.message.sgip12.SgipDeliverResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * Sgip的长短信处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author liuyanning
 */
@Sharable
public class SgipDeliverLongMessageHandler extends AbstractLongMessageHandler<SgipDeliverRequestMessage> {


    @Override
    protected boolean needHandleLongMessage(SgipDeliverRequestMessage msg) {
        return true;
    }

    @Override
    protected IMessage responseErr(SgipDeliverRequestMessage msg) {
        SgipDeliverResponseMessage responseMessage = new SgipDeliverResponseMessage(msg.getHeader());
        //消息结构错误
        responseMessage.setResult((short) 1);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SgipDeliverRequestMessage msg) {
        return msg.getUserNumber() + msg.getSpNumber() + ".";
    }
}
