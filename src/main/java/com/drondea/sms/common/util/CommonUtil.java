package com.drondea.sms.common.util;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.conf.SocketConfig;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.message.slice.LongMessageSliceManager;
import com.drondea.sms.thirdparty.AbstractSmsDcs;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsMessage;
import com.drondea.sms.thirdparty.SmsTextMessage;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.SignatureDirection;
import com.drondea.sms.type.SignaturePosition;
import io.netty.channel.Channel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 公用的工具类
 *
 * @author liuyanning
 */
public final class CommonUtil {

    private final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * cmpp根据用户名、密码、时间戳生成校验字节码
     *
     * @param userName
     * @param password
     * @param timestamp
     * @param charset
     * @return
     */
    public static byte[] getAuthenticatorSource(String userName, String password, String timestamp, Charset charset) {
        byte[] userBytes = userName.getBytes(charset);
        byte[] passwordBytes = password.getBytes(charset);
        byte[] timestampBytes = timestamp.getBytes(charset);
        return DigestUtils.md5(concat(userBytes, new byte[9], passwordBytes, timestampBytes));
    }

    /**
     * smgp根据用户名、密码、时间戳生成校验字节码
     *
     * @param userName
     * @param password
     * @param timestamp
     * @param charset
     * @return
     */
    public static byte[] getAuthenticatorSourceSmgp(String userName, String password, String timestamp, Charset charset) {
        byte[] userBytes = userName.getBytes(charset);
        byte[] passwordBytes = password.getBytes(charset);
        byte[] timestampBytes = timestamp.getBytes(charset);
        return DigestUtils.md5(concat(userBytes, new byte[7], passwordBytes, timestampBytes));
    }

    /**
     * 将字节数组拼接起来
     *
     * @param arrays
     * @return
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    public static byte[] ensureLength(byte[] array, int minLength) {
        return ensureLength(array, minLength, 0);
    }

    /**
     * 保证byte数组的长度
     *
     * @param array     原数组
     * @param minLength 最小长度
     * @param padding   扩展长度
     * @return
     */
    public static byte[] ensureLength(byte[] array, int minLength, int padding) {
        if (array.length == minLength) {
            return array;
        }
        return array.length > minLength ? copyOf(array, minLength) : copyOf(array, minLength + padding);
    }

    /**
     * original扩展为length长度的数组，右侧默认0
     *
     * @param original
     * @param length
     * @return
     */
    private static byte[] copyOf(byte[] original, int length) {
        byte copy[] = new byte[length];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
        return copy;
    }

    public static SmsTextMessage buildTextMessage(String text) {
        return new SmsTextMessage(text);
    }

    public static SmsTextMessage buildSmppTextMessage(String text) {
        return new SmsTextMessage(text, true);
    }

    public static SmsTextMessage buildTextMessage(String text, AbstractSmsDcs smsDcs) {
        return new SmsTextMessage(text, smsDcs);
    }

    public static ChannelSession getChannelSession(Channel channel) {
        ChannelSession channelSession = channel.attr(CmppConstants.NETTY_SESSION_KEY).get();
        return channelSession;
    }

    public static int byteToInt(byte b) {
        return (int) (b & 0x0ff);
    }

    /**
     * ASCII和LATIN1都是单字节编码采用ISO_8859_1,ISO_8859_1向下兼容ASCII,单字节编码
     * UCS2双字节编码采用UTF_16BE
     * RESERVED 预留采用GBK编码
     *
     * @param type
     * @return
     */
    public static Charset switchCharset(SmsAlphabet type) {
        switch (type) {
            case ASCII:
            case LATIN1:
                return StandardCharsets.ISO_8859_1;
            case UCS2:
                return StandardCharsets.UTF_16BE;
            case GBK:
                return Charset.forName("GBK");
            case RESERVED:
                return Charset.forName("GBK");
            default:
                return CmppConstants.DEFAULT_TRANSPORT_CHARSET;
        }
    }

    public static List<IMessage> getLongMsgSlices(ILongSMSMessage msg, SocketConfig configuration, SequenceNumber sequenceNumber) {

        List<IMessage> messageList = new ArrayList<>();
        SmsMessage msgContent = msg.getSmsMessage();

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
            messageList.add((IMessage) msg);
            return messageList;
        }
        int i = 0;
        for (LongMessageSlice frame : longMessageSlices) {
            int sequenceId = msg.getSequenceNum();
            if (i != 0) {
                sequenceId = sequenceNumber.next();
            }
            i++;
            try {
                IMessage longMsg = (IMessage) msg.generateMessage(frame, sequenceId);
                messageList.add(longMsg);
            } catch (Exception e) {
                logger.error("generateMessage err:{}", e.getMessage());
            }

        }
        return messageList;
    }

    private static boolean checkRemoveSignature(SignatureDirection signatureDirection, SignaturePosition signaturePosition,
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

}
