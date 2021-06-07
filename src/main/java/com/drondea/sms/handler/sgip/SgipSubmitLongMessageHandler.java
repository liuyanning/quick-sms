package com.drondea.sms.handler.sgip;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.sgip12.SgipSubmitRequestMessage;
import com.drondea.sms.message.sgip12.SgipSubmitResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * Sgip的长短信处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author liuyanning
 */
@Sharable
public class SgipSubmitLongMessageHandler extends AbstractLongMessageHandler<SgipSubmitRequestMessage> {


    @Override
    protected boolean needHandleLongMessage(SgipSubmitRequestMessage msg) {
        return true;
    }

    @Override
    protected IMessage responseErr(SgipSubmitRequestMessage msg) {
        SgipSubmitResponseMessage responseMessage = new SgipSubmitResponseMessage(msg.getHeader());
        //消息结构错误
        responseMessage.setResult((short) 1);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SgipSubmitRequestMessage msg) {
        return StringUtils.join(msg.getUserNumber(), "|") + msg.getSpNumber() + ".";
    }
}