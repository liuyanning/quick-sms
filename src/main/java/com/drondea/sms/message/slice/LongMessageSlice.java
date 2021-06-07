package com.drondea.sms.message.slice;

import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.type.CmppConstants;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.thirdparty.AbstractSmsDcs;
import com.drondea.sms.thirdparty.SmsUdhIei;

import java.util.ArrayList;
import java.util.List;

/**
 * 短信片段对象
 *
 * @author liu
 */
public class LongMessageSlice {
    private static final long serialVersionUID = -8554060199834235624L;
    private short pkTotal = 1;
    private short pkNumber = 1;
    private short tpPid = 0;
    private short tpUdhi = 0;
    private AbstractSmsDcs msgFmt = CmppConstants.DEFAULT_MSG_FMT;
    private short msgLength = 140;
    /**
     * 长短信字节内容
     */
    private byte[] msgContentBytes = GlobalConstants.EMPTY_BYTE;

    private String contentPart;

    private long sequence;

    private UserDataHeader userDataHeader;


    /**
     * 用户头部数据位数7字节还是6字节
     */
    private SliceType sliceType;
    /**
     * 所有短信片段的key
     */
    private int frameKey;
    /**
     * 分片总共有几条短信
     */
    private int frameLength = 0;
    /**
     * 第几条短信
     */
    private int frameIndex;

    /**
     * 用户数据的头部对象
     **/
    private class UserDataHeader {
        int headerlength;
        List<InformationElement> infoElement;
    }


    /**
     * 6个字节的TP_udhi协议头
     * 05 00 03 XX MM NN
     * byte 1 : 05, 表示剩余协议头的长度
     * byte 2 : 00, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为1（格式中的XX值）。
     * byte 3 : 03, 这个值表示剩下短信标识的长度
     * byte 4 : XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯一并不是很 重要。
     * byte 5 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。
     * byte 6 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。
     * 例如：05 00 03 39 02 01
     * <p>
     * 7个字节的TP_udhi协议头
     * 06 08 04 XX XX MM NN
     * byte 1 : 06, 表示剩余协议头的长度
     * byte 2 : 08, 这个值在GSM 03.40规范9.2.3.24.1中规定，表示随后的这批超长短信的标识位长度为2（格式中的XX值）。
     * byte 3 : 04, 这个值表示剩下短信标识的长度
     * byte 4-5 : XX XX，这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后，就重新记录，所以这个标志是否唯 一并不是很重要。
     * byte 6 : MM, 这批短信的数量。如果一个超长短信总共5条，这里的值就是5。
     * byte 7 : NN, 这批短信的数量。如果当前短信是这批短信中的第一条的值是1，第二条的值是2。
     * 例如：06 08 04 00 39 02 01
     *
     * @return
     */
    public void parseUserDataHeader() {
        UserDataHeader udh = new UserDataHeader();
        //头数据长度，05
        udh.headerlength = msgContentBytes[0];
        udh.infoElement = new ArrayList<>();

        int i = 1;
        while (i < udh.headerlength) {
            InformationElement t = new InformationElement();
            // 00 头部数据类型
            t.udhIei = SmsUdhIei.valueOf(msgContentBytes[i++]);
            // 03 头部数据长度
            t.infoEleLength = msgContentBytes[i++];
            t.infoEleData = new byte[t.infoEleLength];
            if (t.infoEleLength > 0) {
                System.arraycopy(msgContentBytes, i, t.infoEleData, 0, t.infoEleLength);
                i += t.infoEleLength;
            }
            udh.infoElement.add(t);

            if (SmsUdhIei.CONCATENATED_8BIT.equals(t.udhIei)) {
                setSliceType(SliceType.SIX);
                //这批短信的唯一标识
                setFrameKey(CommonUtil.byteToInt(t.infoEleData[0]));

                int pkTotal = CommonUtil.byteToInt(t.infoEleData[1]);
                setFrameLength(pkTotal);
                setPkTotal((short) pkTotal);

                int pkNumber = CommonUtil.byteToInt(t.infoEleData[2]);
                //第n条短信
                setFrameIndex(pkNumber - 1);
                setPkNumber((short) pkNumber);
            } else if (SmsUdhIei.CONCATENATED_16BIT.equals(t.udhIei)) {
                setSliceType(SliceType.SEPTET);
                //这批短信的唯一标识,这里是7字节的情况，key是两位的
                frameKey = (((t.infoEleData[0] & 0xff) << 8) | (t.infoEleData[1] & 0xff)) & 0x0ffff;
                int pkTotal = CommonUtil.byteToInt(t.infoEleData[2]);
                setFrameLength(pkTotal);
                setPkTotal((short) pkTotal);

                int pkNumber = CommonUtil.byteToInt(t.infoEleData[3]);
                //第n条短信
                setFrameIndex(pkNumber - 1);
                setPkNumber((short) pkNumber);
            }
        }
        this.userDataHeader = udh;
    }

    /**
     * @return the pktotal
     */
    public short getPkTotal() {
        return pkTotal;
    }

    /**
     * @param pkTotal the pktotal to set
     */
    public void setPkTotal(short pkTotal) {
        this.pkTotal = pkTotal;
    }

    /**
     * @return the pknumber
     */
    public short getPkNumber() {
        return pkNumber;
    }

    /**
     * @param pkNumber the pknumber to set
     */
    public void setPkNumber(short pkNumber) {
        this.pkNumber = pkNumber;
    }

    short getTpPid() {
        return tpPid;
    }

    public void setTpPid(short tpPid) {
        this.tpPid = tpPid;
    }

    /**
     * @return the tpudhi
     */
    public short getTpUdhi() {
        return tpUdhi;
    }

    /**
     * @param tpUdhi the tpudhi to set
     */
    public void setTpudhi(short tpUdhi) {
        this.tpUdhi = tpUdhi;
    }

    /**
     * @return the msgfmt
     */
    public AbstractSmsDcs getMsgFmt() {
        return msgFmt;
    }

    /**
     * @param msgFmt the msgfmt to set
     */
    public void setMsgFmt(AbstractSmsDcs msgFmt) {
        this.msgFmt = msgFmt;
    }

    /**
     * @return the msgLength
     */
    public short getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    /**
     * @return the msgContentBytes
     */
    public byte[] getMsgContentBytes() {
        return msgContentBytes;
    }

    /**
     * @param msgContentBytes the msgContentBytes to set
     */
    public void setMsgContentBytes(byte[] msgContentBytes) {
        this.msgContentBytes = msgContentBytes;
    }

    public String getContentPart() {
        return contentPart;
    }

    public void setContentPart(String contentPart) {
        this.contentPart = contentPart;
    }


    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public UserDataHeader getUserDataHeader() {
        return userDataHeader;
    }

    public void setUserDataHeader(UserDataHeader userDataHeader) {
        this.userDataHeader = userDataHeader;
    }

    public int getFrameKey() {
        return frameKey;
    }

    public void setFrameKey(int frameKey) {
        this.frameKey = frameKey;
    }

    public int getFrameLength() {
        return frameLength;
    }

    public void setFrameLength(int frameLength) {
        this.frameLength = frameLength;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    public SliceType getSliceType() {
        return sliceType;
    }

    public void setSliceType(SliceType sliceType) {
        this.sliceType = sliceType;
    }

    /**
     * 获取短信内容字节数组
     *
     * @return
     */
    public byte[] getPayloadbytes() {
        if (this.tpUdhi > 0) {
            if (this.getUserDataHeader() == null) {
                this.parseUserDataHeader();
            }
            int headerLength = this.getUserDataHeader().headerlength + 1;
            //内容长度，总长度 - 头数据长度
            int payloadLength = msgLength - headerLength;
            byte[] payload = new byte[payloadLength];
            System.arraycopy(msgContentBytes, headerLength, payload, 0, payloadLength);
            return payload;
        } else {
            return msgContentBytes;
        }
    }
}
