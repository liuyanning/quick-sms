package com.drondea.sms.handler.smgp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitResponseMessage;

import org.apache.commons.lang3.StringUtils;

import io.netty.channel.ChannelHandler.Sharable;

/**
 * smgp的长短信处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author ywj
 */
@Sharable
public class SmgpSubmitLongMessageHandler extends AbstractLongMessageHandler<SmgpSubmitRequestMessage> {


    @Override
    protected boolean needHandleLongMessage(SmgpSubmitRequestMessage msg) {
        return true;//SMGP需要处理长短信，set msgLength、msgContentBytes等
    }

    @Override
    protected IMessage responseErr(SmgpSubmitRequestMessage msg) {
        SmgpSubmitResponseMessage responseMessage = new SmgpSubmitResponseMessage();
        responseMessage.getHeader().setSequenceId(msg.getSequenceId());
        //消息结构错误
        responseMessage.setStatus(10);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SmgpSubmitRequestMessage msg) {
//        return StringUtils.join(msg.getDestTerminalId(), "|") + msg.getSrcId() + ".";
        return StringUtils.join(msg.getDestTermIdArray(), "|") + msg.getSrcTermId() + ".";
    }

}
