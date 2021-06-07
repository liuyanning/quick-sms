package com.drondea.sms.message.slice;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.common.util.SystemClock;
import com.drondea.sms.message.ILongSMSMessage;
import com.drondea.sms.type.NotSupportedException;
import org.apache.commons.lang3.time.DateFormatUtils;
import com.drondea.sms.thirdparty.AbstractSmsDcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;

// 用来保存一条短信的各个片断

/**
 * TP_udhi ：0代表内容体里不含有协议头信息
 * 1代表内容含有协议头信息（长短信，push短信等都是在内容体上含有头内容的）当设置内容体包含协议头
 * ，需要根据协议写入相应的信息，长短信协议头有两种：<br/>
 * 6位协议头格式：05 00 03 XX MM NN<br/>
 * byte 1 : 05, 表示剩余协议头的长度<br/>
 * byte 2 : 00, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为1（格式中的XX值）。<br/>
 * byte 3 : 03, 这个值表示剩下短信标识的长度<br/>
 * byte 4 : XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯 一并不是很 重要。<br/>
 * byte 5 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。<br/>
 * byte 6 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。<br/>
 * 例如：05 00 03 39 02 01 <br/>
 * <p>
 * 7 位的协议头格式：06 08 04 XX XX MM NN<br/>
 * byte 1 : 06, 表示剩余协议头的长度<br/>
 * byte 2 : 08, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为2（格式中的XX值）。<br/>
 * byte 3 : 04, 这个值表示剩下短信标识的长度<br/>
 * byte 4-5 : XX
 * XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯一并不是很重要。<br/>
 * byte 6 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。<br/>
 * byte 7 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。<br/>
 * 例如：06 08 04 00 39 02 01 <br/>
 **/

class SliceContainer {
    private static final Logger logger = LoggerFactory.getLogger(SliceContainer.class);

    /**
     * 这个字段目前只在当分片丢失时方便跟踪
     */
    private String serviceNum;
    private long sequence;
    private long timestamp = SystemClock.now();
    /**
     * 长短信的总分片数量
     */
    private int totalLength = 0;
    int frameKey;
    /**
     * 保存帧的Map,每帧都有一个唯一码。以这个唯一码做key
     */
    private byte[][] content;

    private int totalByteLength = 0;

    private BitSet idxBitSet;

    private AbstractSmsDcs msgFmt;

    private InformationElement appUDHinfo;

    private ILongSMSMessage msg;

    /**
     * 业务自定义的批次号，同一条长短信相同
     */
    private String batchNumber;

    /**
     * 用来保存应用类型，如文本短信或者wap短信
     *
     * @param appUDHinfo
     */
    public void setAppUDHinfo(InformationElement appUDHinfo) {
        this.appUDHinfo = appUDHinfo;
    }

    public InformationElement getAppUDHinfo() {
        return this.appUDHinfo;
    }

    public void setServiceNum(String serviceNum) {
        this.serviceNum = serviceNum;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SliceContainer(int frameKey, int totalLength) {
        this.frameKey = frameKey;
        this.totalLength = totalLength;

        this.content = new byte[totalLength][];
        this.idxBitSet = new BitSet(totalLength);
    }

    /**
     * 将短信片段合并到FrameHolder
     *
     * @param slice
     * @return 是否合并完毕
     * @throws NotSupportedException
     */
    public boolean mergeFrameHolder(LongMessageSlice slice) throws NotSupportedException {
        return this.merge(slice, slice.getPayloadbytes(), slice.getFrameIndex());
    }

    /**
     * 合并长短信
     *
     * @param frame   长短信一个片段
     * @param content 片段的短信内容
     * @param idx     第几个片段
     * @return     是否合并完成
     * @throws NotSupportedException
     */
    public synchronized boolean merge(LongMessageSlice frame, byte[] content, int idx) throws NotSupportedException {

        if (idxBitSet.get(idx)) {
            logger.warn("have received the same index:{} of Message. do not merge this content.{},origin:{},{},{},new content:{}", idx, this.serviceNum,
                    LongMessageSliceManager.buildTextMessage(this.content[idx], msgFmt).getText(), DateFormatUtils.format(getTimestamp(),
                            DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()), getSequence(), LongMessageSliceManager.buildTextMessage(content, msgFmt).getText());
            throw new NotSupportedException("received the same index");
        }
        if (this.content.length <= idx || idx < 0) {
            logger.warn("have received error index:{} of Message content length:{}. do not merge this content.{},{},{},{}", idx, this.content.length,
                    this.serviceNum, DateFormatUtils.format(getTimestamp(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()), getSequence(),
                    LongMessageSliceManager.buildTextMessage(content, msgFmt).getText());
            throw new NotSupportedException("have received error index");
        }
        // 设置该短信序号已填冲
        idxBitSet.set(idx);

        //判断不同分片的msgfmt是否相同，不同的话就当成String进行编码转换
        if (this.msgFmt != null && msgFmt.getValue() != frame.getMsgFmt().getValue()) {
            String txt = new String(content, CommonUtil.switchCharset(frame.getMsgFmt().getAlphabet()));
            this.content[idx] = txt.getBytes(CommonUtil.switchCharset(msgFmt.getAlphabet()));
        } else {
            this.content[idx] = content;
        }

        this.totalByteLength += this.content[idx].length;

        return totalLength == idxBitSet.cardinality();
    }

    public synchronized boolean isComplete() {
        return totalLength == idxBitSet.cardinality();
    }

    public synchronized byte[] mergeAllContent() {
        if (totalLength == 1) {
            return content[0];
        }
        byte[] ret = new byte[totalByteLength];
        int idx = 0;
        for (int i = 0; i < totalLength; i++) {
            if (content[i] != null && content[i].length > 0) {
                System.arraycopy(content[i], 0, ret, idx, content[i].length);
                idx += content[i].length;
            }
        }

        return ret;
    }

    public AbstractSmsDcs getMsgFmt() {
        return msgFmt;
    }

    public void setMsgFmt(AbstractSmsDcs msgFmt) {
        this.msgFmt = msgFmt;
    }

    public ILongSMSMessage getMsg() {
        return msg;
    }

    public void setMsg(ILongSMSMessage msg) {
        this.msg = msg;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
}
