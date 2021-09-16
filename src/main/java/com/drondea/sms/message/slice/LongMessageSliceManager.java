package com.drondea.sms.message.slice;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.thirdparty.*;
import com.drondea.sms.type.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//短信片断持久化需要集中保存，因为同一短信的不同分片会从不同的连接发送。可能不在同一台主机。
//可以使用 Redis.Memcached等。

/**
 * 长短信的容器
 *
 * @author liuyanning
 */
public class LongMessageSliceManager {

    private static final Logger logger = LoggerFactory.getLogger(LongMessageSliceManager.class);


    private static SmsMessage generatorSmsMessage(SliceContainer fh) {
        byte[] contents = fh.mergeAllContent();
        //文本短信
        return buildTextMessage(contents, fh.getMsgFmt());
    }

    public static SmsTextMessage buildTextMessage(byte[] bytes, AbstractSmsDcs msgFmt) {
        String text = null;
        //GSM是7字节编码
        switch (msgFmt.getAlphabet()) {
            case GSM:
                //不压缩版本,只是对7字节符号进行编译
                text = SmsPduUtil.unencodedSeptetsToString(bytes);
                break;
//                //7字节压缩解码
//                text = CharsetUtil.map(CharsetUtil.NAME_PACKED_GSM).decode(bytes);
//                break;
            default:
                text = new String(bytes, CommonUtil.switchCharset(msgFmt.getAlphabet()));
        }
        return new SmsTextMessage(text, msgFmt);
    }


    /**
     * 进行短信拼接，获取一条完整的长短信，如果长短信组装未完成，返回null
     **/
    public static SmsMessage reassemble(String serviceNum, ILongSMSMessage msg, IBatchNumberCreator batchNumberCreator) throws NotSupportedException {

        //创建短信片段对象
        LongMessageSlice slice = msg.generateSlice();
        // udhi只取第一个bit
        if ((slice.getTpUdhi() & 0x01) == 0) {
            String batchNumber = batchNumberCreator.generateBatchNumber();
            msg.setBatchNumber(batchNumber);
            // 短信内容不带协议头，直接获取短信内容
            SmsMessage smsMsg = buildTextMessage(slice.getPayloadbytes(), slice.getMsgFmt());
            return smsMsg;
        } else if ((slice.getTpUdhi() & 0x01) == 1) {
            try {
                //处理短信片段的头部信息
                slice.parseUserDataHeader();
                //长短信长度为1
                if (slice.getFrameLength() == 1) {
                    String batchNumber = batchNumberCreator.generateBatchNumber();
                    msg.setBatchNumber(batchNumber);
                    SmsMessage smsMsg = buildTextMessage(slice.getPayloadbytes(), slice.getMsgFmt());
                    return smsMsg;
                }
                String key = new StringBuilder().append(serviceNum).append(slice.getFrameKey()).toString();
                IDBStore dbStore = GlobalConstants.dbStore;
                SliceContainer sliceContainer = (SliceContainer) dbStore.get(key);
                //判断key是否存在，已经存在，合并短信，不存在就创建FrameHolder并保存
                if (sliceContainer == null) {

                    sliceContainer = createSliceContainer(serviceNum, slice);
                    //生成新的batchNumber
                    String batchNumber = batchNumberCreator.generateBatchNumber();
                    sliceContainer.setBatchNumber(batchNumber);
                    sliceContainer.setMsg(msg);

                    //多线程防止并发问题
                    SliceContainer prevContainer = (SliceContainer) dbStore.putIfAbsent(key, sliceContainer);
                    if (prevContainer != null) {
                        sliceContainer = prevContainer;
                    }
                }

                //合并片段
                boolean isComplete = sliceContainer.mergeFrameHolder(slice);
                msg.setBatchNumber(sliceContainer.getBatchNumber());

                //后续收到的片断加入列表
                sliceContainer.getMsg().addFragment(msg);
                //没有pkNumber字段的要补上
                if (slice.getPkTotal() != msg.getPkTotal()) {
                    msg.setPkNumber(slice.getPkNumber());
                    msg.setPkTotal(slice.getPkTotal());
                }

                if (isComplete) {
                    dbStore.remove(key);
                    return generatorSmsMessage(sliceContainer);
                }
            } catch (Exception ex) {
                logger.error("", ex);
                return null;
            }

        } else {
            throw new NotSupportedException("Not Support LongMsg.Tpudhi");
        }
        return null;
    }

    private static SliceContainer createSliceContainer(String serviceNum, LongMessageSlice frame) {

        //这批短信的总条数
        SliceContainer sliceContainer = new SliceContainer(frame.getFrameKey(), frame.getFrameLength());

        sliceContainer.setMsgFmt(frame.getMsgFmt());
        sliceContainer.setSequence(frame.getSequence());
        sliceContainer.setServiceNum(serviceNum);
        return sliceContainer;
    }

    /**
     * 不用移除签名的调用方式
     *
     * @param content
     * @return
     */
    public static List<LongMessageSlice> splitMsgSlice(SmsMessage content) {
        return splitMsgSlice(content, false, null, null);
    }

    /**
     * @param content    短信
     * @param needRemove 是否移除签名
     * @param position   签名位置
     * @param signature  签名内容
     * @return
     */
    public static List<LongMessageSlice> splitMsgSlice(SmsMessage content, boolean needRemove, SignaturePosition position, String signature) {

        List<LongMessageSlice> result = new ArrayList<>();
        if (content == null) {
            return null;
        }
        //每个pdu是一个短信拆分header和内容
        SmsPdu[] pdus = content.getPdus();

        int pduLength = pdus.length;
        //后置签名移除规则，1只移除最后一个短信的，2，倒数两条短信都要移除
        int sufixRemoveType = 1;
        int sufixRemoveLength = 0;
        //后置签名，可能倒数两个短信都有签名数据
        if (needRemove && position.equals(SignaturePosition.SUFFIX)) {
            int sigLength = getSignatureByteLength(signature, pdus[0].getUserData().getDcs().getAlphabet());

            int lastLength = pdus[pduLength - 1].getUserData().getLength();
            //签名长度比最后一个短信内容长，要倒数第二条也移除
            if (sigLength > lastLength) {
                sufixRemoveType = 2;
                //倒数第二条移除
                sufixRemoveLength = sigLength - lastLength;
            } else {
                //全部移除
                sufixRemoveLength = sigLength;
            }
        }

        if (needRemove && position.equals(SignaturePosition.PREFIX)) {
            int sigLength = getSignatureByteLength(signature, pdus[0].getUserData().getDcs().getAlphabet());
            sufixRemoveLength = sigLength;
        }

        for (int i = 1; i <= pduLength; i++) {
            SmsPdu aMsgPdu = pdus[i - 1];
            //生成UDH数据
            byte[] udh = aMsgPdu.getUserDataHeaders();
            LongMessageSlice frame = new LongMessageSlice();
            frame.setPkTotal((short) pdus.length);
            frame.setPkNumber((short) i);
            frame.setMsgFmt(aMsgPdu.getDcs());

            //如果是null说明一条短信就可以搞定，不用长短信
            frame.setTpudhi(udh != null ? (short) 1 : (short) 0);

            //前置签名移除，前置移除第一个
            if (needRemove && position.equals(SignaturePosition.PREFIX) && i == 1) {
                //短信内容移除前边的sigLength长度的字节
                removePduSigBytes(aMsgPdu, position, sufixRemoveLength);
            }

            //倒数第二条需要后边移除sufixRemoveLength 个字节
            if (needRemove && position.equals(SignaturePosition.SUFFIX) && sufixRemoveType == 2 && i == pduLength - 1) {
                removePduSigBytes(aMsgPdu, position, sufixRemoveLength);
            }

            //最后一条需要移除所有的或者sufixRemoveLength个
            if (needRemove && position.equals(SignaturePosition.SUFFIX) && i == pduLength) {
                //要移除倒数第二条的情况倒数第一条全部移除
                if (sufixRemoveType == 2) {
                    aMsgPdu.setUserData(GlobalConstants.EMPTY_BYTE, 0, aMsgPdu.getUserData().getDcs());
                } else {
                    //去除最后一条短信的sufixRemoveLength长度字节
                    removePduSigBytes(aMsgPdu, position, sufixRemoveLength);
                }
            }

            byte[] contents = encodeOctetPdu(aMsgPdu, udh);
            //将短信头和内容合并
            frame.setMsgLength((short) contents.length);
            frame.setMsgContentBytes(contents);
            result.add(frame);
        }

        return result;
    }

    /**
     * 移除pdu的用户数据中的签名长度的字节
     *
     * @param smsPdu
     * @param signaturePosition 签名方向
     * @param removeLength
     */
    public static void removePduSigBytes(SmsPdu smsPdu, SignaturePosition signaturePosition, int removeLength) {
        byte[] data = smsPdu.getUserData().getData();
        int dataLength = smsPdu.getUserData().getLength();
        int newDataLength = dataLength - removeLength;
        byte[] newData = new byte[newDataLength];
        if (signaturePosition.equals(SignaturePosition.PREFIX)) {
            System.arraycopy(data, removeLength, newData, 0, newDataLength);
        } else {
            System.arraycopy(data, 0, newData, 0, newDataLength);
        }
        //重新组合用户数据
        smsPdu.setUserData(newData, newDataLength, smsPdu.getUserData().getDcs());
    }

    public static int getSignatureByteLength(String signature, SmsAlphabet alp) {
        byte[] bs;
        //GSM特殊处理一下
        switch (alp) {
            case GSM:
                bs = SmsPduUtil.stringToUnencodedSeptets(signature);
                break;
            case ASCII:
            case LATIN1:
            case UCS2:
            case GBK:
            case RESERVED:
                bs = signature.getBytes(CommonUtil.switchCharset(alp));
                break;
            default:
                bs = signature.getBytes(CommonUtil.switchCharset(SmsAlphabet.UCS2));
                break;
        }
        return bs.length;
    }


    /**
     * 将pdu的头部数据和短信内容合并到OutputStream
     *
     * @param pdu
     * @return
     */
    private static byte[] encodeOctetPdu(SmsPdu pdu, byte[] udh) {
        SmsUserData userData = pdu.getUserData();
        byte[] ud = userData.getData();
        int length = 0;
        ByteBuf buffer;
        byte[] content;
        int nUdBytes = userData.getLength();
        int nUdhBytes = (udh == null) ? 0 : udh.length;

        // 1 octet/ 7 octets
        // TP-VP - Optional

        // UDH?
        if (nUdhBytes == 0) {
            // 1 Integer
            // TP-UDL
            // UDL includes the length of UDH
            length = nUdBytes;
            buffer = Unpooled.buffer(length);
            content = new byte[length];
            // n octets
            // TP-UD
            buffer.writeBytes(ud);
        } else {

            // TP-UDL includes the length of UDH
            // +1 is for the size header...
            length = nUdBytes + nUdhBytes;
            buffer = Unpooled.buffer(length);
            content = new byte[length];
            // TP-UDH (including user data header length)
            buffer.writeBytes(udh);
            // TP-UD
            buffer.writeBytes(ud);
        }
        buffer.readBytes(content);
        ReferenceCountUtil.release(buffer);

        return content;
    }

    /**
     * 获取长短信切分后的短信片断内容
     **/
    public static String getPartTextMsg(LongMessageSlice slice) {
        return buildTextMessage(slice.getPayloadbytes(), slice.getMsgFmt()).getText();
    }

}
