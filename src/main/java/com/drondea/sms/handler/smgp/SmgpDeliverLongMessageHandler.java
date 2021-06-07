package com.drondea.sms.handler.smgp;

import com.drondea.sms.handler.AbstractLongMessageHandler;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpDeliverRequestMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import org.apache.commons.lang3.StringUtils;

/**
 * smgp的上行短信的处理，对文本短信再次进行编解码，解析文本字符串，拆解文本字符串为长短信
 *
 * @author ywj
 */
@Sharable
public class SmgpDeliverLongMessageHandler extends AbstractLongMessageHandler<SmgpDeliverRequestMessage> {

    @Override
    protected boolean needHandleLongMessage(SmgpDeliverRequestMessage msg) {
        //状态通知的不用处理
        return !msg.isReport();
    }

    @Override
    protected IMessage responseErr(SmgpDeliverRequestMessage msg) {
        SmgpSubmitResponseMessage responseMessage = new SmgpSubmitResponseMessage(msg.getHeader());
        //消息结构错误
        responseMessage.setStatus(10);
        return responseMessage;
    }

    @Override
    protected String generateFrameKey(SmgpDeliverRequestMessage msg) {
        return StringUtils.join(msg.getSrcTermId(), "|") + msg.getDestTermId() + ".";
    }

}
