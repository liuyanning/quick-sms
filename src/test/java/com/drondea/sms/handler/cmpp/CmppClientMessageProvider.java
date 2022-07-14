package com.drondea.sms.handler.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.MessageProvider;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 一个全局的provider
 * 实际应用中可根据一个clien创建一个MessageProvider
 */
public class CmppClientMessageProvider implements MessageProvider {

    private ConcurrentLinkedQueue<ILongSMSMessage> MESSAGES_QUEUE = new ConcurrentLinkedQueue();
    @Override
    public List<IMessage> getTcpMessages(ChannelSession channelSession) {
        ILongSMSMessage requestMessage = MESSAGES_QUEUE.poll();
        if (requestMessage == null) {
            return null;
        }
        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
        //切分长短信
        List<IMessage> longMsgSlices = CommonUtil.getLongMsgSlices(requestMessage, channelSession.getConfiguration(), sequenceNumber);
        return longMsgSlices;
    }

    @Override
    public void responseMessageMatchFailed(String requestKey, IMessage response) {

    }

    /**
     * 发送短信
     * @param message
     * @return
     */
    public boolean sendMessage(ILongSMSMessage message) {
        return MESSAGES_QUEUE.offer(message);
    }
}
