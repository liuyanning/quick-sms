package com.drondea.sms.handler;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.MsgId;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.SmsMessage;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.SignatureDirection;
import com.drondea.sms.type.SignaturePosition;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @version V3.0.0
 * @description: 长短信拆分和处理
 * @author: 刘彦宁
 * @date: 2020年06月12日16:38
 **/
public abstract class AbstractLongMessageHandler<T extends ILongSMSMessage> extends MessageToMessageCodec<T, T> {

    private final Logger logger = LoggerFactory.getLogger(AbstractLongMessageHandler.class);

    /**
     * 将长短信解码，并将内容转换成字符串
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, T msg, List<Object> out) throws Exception {
        logger.debug("long message decode {}", msg);
        if (!needHandleLongMessage(msg)) {
            out.add(msg);
            return;
        }

        //根据短信内容生成key
        String key = generateFrameKey(msg);
        try {
            //进行短信的组合，如果组合成功返回，未成功返回null
            SmsMessage smsMessage = LongMessageSliceManager.reassemble(key, msg, () -> generateBatchNumber());
            if (smsMessage != null) {
                msg.setSmsMsg(smsMessage);
            }
            out.add(msg);
        } catch (Exception ex) {
            logger.error("", ex);
            // 长短信解析失败，直接给网关回复 resp . 并丢弃这个短信
            logger.error("Decode Message Error ,msg dump :{}", ByteBufUtil.hexDump(msg.generateSlice().getMsgContentBytes()));
            IMessage res = responseErr(msg);
            ctx.writeAndFlush(res);
        }
    }


    /**
     * 生成批次号
     *
     * @return
     */
    public String generateBatchNumber() {
        return new MsgId().toString();
    }

    /**
     * 编码短信字符串为字节码内容
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, List<Object> out) throws Exception {
        logger.debug("long message encode {}", msg);
        if (!needHandleLongMessage(msg)) {
            out.add(msg);
            return;
        }
        SmsMessage msgContent = msg.getSmsMessage();
        ChannelSession channelSession = CommonUtil.getChannelSession(ctx.channel());
        SocketConfig configuration = channelSession.getConfiguration();

        //是否要移除签名
        boolean isRemoveSignature = false;
        String smsSignature = GlobalConstants.EMPTY_STRING;
        SignaturePosition signaturePosition = SignaturePosition.PREFIX;

        //是客户端的话要看是否有移除签名的配置
        if (configuration instanceof ClientSocketConfig) {
            ClientSocketConfig clientSocketConfig = (ClientSocketConfig) configuration;
            smsSignature = msg.getMsgSignature();
            signaturePosition = clientSocketConfig.getSignaturePosition();
            isRemoveSignature = checkRemoveSignature(clientSocketConfig.getSignatureDirection(), signaturePosition, smsSignature, msgContent);
        }

        List<LongMessageSlice> longMessageSlices;
        if (isRemoveSignature) {
            longMessageSlices = LongMessageSliceManager.splitMsgSlice(msgContent, true, signaturePosition, smsSignature);
        } else {
            longMessageSlices = LongMessageSliceManager.splitMsgSlice(msgContent);
        }
        if (longMessageSlices == null) {
            out.add(msg);
            return;
        }
        //此连接的SequenceNumber生成器
        SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
        int i = 0;
        for (LongMessageSlice frame : longMessageSlices) {
            int sequenceId = msg.getSequenceNum();
            if (i != 0) {
                sequenceId = sequenceNumber.next();
            }
            i++;
            T longMsg = (T) msg.generateMessage(frame, sequenceId);
            out.add(longMsg);
        }
    }

    /**
     * 是否要移除签名
     *
     * @param signatureDirection
     * @param signaturePosition
     * @param smsSignature
     * @param msgContent
     * @return
     */
    private boolean checkRemoveSignature(SignatureDirection signatureDirection, SignaturePosition signaturePosition,
                                         String smsSignature, SmsMessage msgContent) {
        if (SignatureDirection.CUSTOM.equals(signatureDirection)) {
            return false;
        }

        if ((StringUtils.isEmpty(smsSignature) || signaturePosition == null)) {
            logger.error("removeSignature is true,you need set params smsSignature and signaturePosition");
            //配置移除签名了但是配置项目有空的，不移除
            return false;
        }

        //如果是前置签名，内容不以签名开头的不需要移除
        if (signaturePosition.equals(SignaturePosition.PREFIX) &&
                !msgContent.toString().startsWith(smsSignature)) {
            return false;
        }

        if (signaturePosition.equals(SignaturePosition.SUFFIX) &&
                !msgContent.toString().endsWith(smsSignature)) {
            return false;
        }
        return true;
    }


    /**
     * 是否需要处理长短信和文字编码处理，对于cmpp的deliver短信，如果是状态报告就不要处理这里
     *
     * @param msg
     * @return
     */
    protected abstract boolean needHandleLongMessage(T msg);

    /**
     * 解析长短信失败后响应客户端
     *
     * @param msg
     * @return
     */
    protected abstract IMessage responseErr(T msg);

    /**
     * 根据短信内容生成key
     *
     * @param msg
     * @return
     * @throws Exception
     */
    protected abstract String generateFrameKey(T msg);
}
