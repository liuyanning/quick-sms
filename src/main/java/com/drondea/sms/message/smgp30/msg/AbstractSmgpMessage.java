package com.drondea.sms.message.smgp30.msg;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.message.smgp30.tlv.SmgpTLV;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.IMessageResponseHandler;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V3.0
 * @description: smgp包父类
 * @author: ywj
 * @date: 2020年06月08日14:23
 **/
public abstract class AbstractSmgpMessage implements IMessage, Cloneable {

    private IPackageType packageType;
    private long timestamp;
    /**
     * 消息的生命周期，单位秒, 0表示永不过期
     */
    private long lifeTime = 0;
    protected List<SmgpTLV> optionalParameters = new ArrayList<SmgpTLV>();

    private SmgpHeader header;

    /**
     * 发送重试次数
     */
    private int retryCount = 0;

    private IMessageResponseHandler messageResponseHandler;
    private IMessage requestMessage;
    private long sendTimestamp;

    public AbstractSmgpMessage(SmgpPackageType packageType) {
        setPackageType(packageType);
        SmgpHeader header = new SmgpHeader(packageType);
        header.setCommandId(packageType.getCommandId());
        setHeader(header);
    }

    public AbstractSmgpMessage(IPackageType packageType, SmgpHeader header) {
        setPackageType(packageType);
        if (header == null) {
            header = new SmgpHeader(packageType.getCommandId());

            header.setSequenceId(GlobalConstants.smgpSequenceNumber.next());
        } else {
            header.setCommandId(packageType.getCommandId());
        }
        setHeader(header);
    }

    public IPackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(IPackageType packageType) {
        this.packageType = packageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public SmgpHeader getHeader() {
        return header;
    }

    public void setHeader(SmgpHeader header) {
        this.header = header;
    }

    public int getHeaderLength() {
        return getHeader().getHeadLength();
    }

    /**
     * 获取消息的body长度
     *
     * @return
     */
    public abstract int getBodyLength();

    /**
     * 把header也拷贝一份，本身clone是浅拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public AbstractSmgpMessage clone() throws CloneNotSupportedException {
        AbstractSmgpMessage abstractSmgpMessage = (AbstractSmgpMessage) super.clone();
        SmgpHeader smgpHeader = this.header.clone();
        abstractSmgpMessage.setHeader(smgpHeader);
        //重新生成list，避免浅克隆引用
        List<SmgpTLV> optionalParameters = new ArrayList<SmgpTLV>();
        for (SmgpTLV tlv : this.optionalParameters) {
            optionalParameters.add(tlv);
        }
        abstractSmgpMessage.optionalParameters = optionalParameters;
        return abstractSmgpMessage;
    }

    @Override
    public int getSequenceId() {
        return getHeader().getSequenceId();
    }

    @Override
    public boolean isWindowResponseMessage() {
        return false;
    }

    @Override
    public boolean isWindowSendMessage() {
        return false;
    }

    @Override
    public int addRetryCount() {
        return ++retryCount;
    }

    @Override
    public void getRetryCount() {

    }

    @Override
    public boolean isRequest() {
        return getHeader().isRequest();
    }

    @Override
    public void handleMessageComplete(IMessage request, IMessage response) {
        IMessageResponseHandler messageResponseHandler = getMessageResponseHandler();
        if (messageResponseHandler != null) {
            messageResponseHandler.messageComplete(request, response);
        }
    }

    @Override
    public void handleMessageSendFailed(IMessage request) {
        IMessageResponseHandler messageResponseHandler = getMessageResponseHandler();
        if (messageResponseHandler != null) {
            messageResponseHandler.sendMessageFailed(request);
        }
    }

    @Override
    public void handleMessageExpired(String cachedKey, IMessage request) {
        IMessageResponseHandler messageCompleteHandler = getMessageResponseHandler();
        if (messageCompleteHandler != null) {
            messageCompleteHandler.messageExpired(cachedKey, request);
        }
    }

    public IMessageResponseHandler getMessageResponseHandler() {
        return messageResponseHandler;
    }

    @Override
    public void setMessageResponseHandler(IMessageResponseHandler messageResponseHandler) {
        this.messageResponseHandler = messageResponseHandler;
    }

    @Override
    public void setRequestMessage(IMessage iMessage) {
        this.requestMessage = iMessage;
    }

    @Override
    public IMessage getRequestMessage() {
        return this.requestMessage;
    }

    /**
     * 可选参数tlv部分 编码
     *
     * @param bodyBuffer
     * @return
     * @throws Exception
     */
    public ByteBuf encodeTLV(ByteBuf bodyBuffer) throws Exception {
        if (this.optionalParameters != null && optionalParameters.size() > 0) {
            for (SmgpTLV tlv : optionalParameters) {
                if (tlv.hasValue()) {
                    bodyBuffer.writeShort(tlv.getTag());
                    bodyBuffer.writeShort(tlv.getLength());
                    bodyBuffer.writeBytes(tlv.getValueData());
                }
            }
        }
        return bodyBuffer;
    }

    /**
     * 可选参数tlv部分 解码
     *
     * @param bodyBuffer
     * @throws Exception
     */
    public void decodeTLV(ByteBuf bodyBuffer) throws Exception {
        while (bodyBuffer.readableBytes() > 0) {
            short tag = bodyBuffer.readShort();
            short length = bodyBuffer.readShort();
            byte[] valveBytes = new byte[length];
            bodyBuffer.readBytes(valveBytes);
            SmgpTLV tlv = findTLVByTag(tag);
            if (tlv != null) {
                tlv.setValueData(valveBytes);
            }
        }
    }

    /**
     * 根据 tag 获取 optionalParameters 中的 tlv
     *
     * @param tag
     * @return
     */
    protected SmgpTLV findTLVByTag(short tag) {
        if (this.optionalParameters.size() > 0) {
            for (SmgpTLV tlv : this.optionalParameters) {
                if (tlv != null && tlv.getTag() == tag) {
                    return tlv;
                }
            }
        }
        return null;
    }

    protected void replaceTLVByTag(SmgpTLV replace) {
        if (this.optionalParameters.size() > 0) {
            for (int i = 0; i < this.optionalParameters.size(); i++) {
                SmgpTLV tlv = this.optionalParameters.get(i);
                if (tlv != null && tlv.getTag() == replace.getTag()) {
                    this.optionalParameters.set(i, replace);
                    break;
                }
            }
        }
    }

    /**
     * 添加 tlv
     *
     * @param smgpTlv
     */
    public void registerOptional(SmgpTLV smgpTlv) {
        if (smgpTlv != null) {
            if (this.optionalParameters == null) {
                optionalParameters = new ArrayList<>();
            }
            optionalParameters.add(smgpTlv);
        }
    }

    /**
     * tlv 是否已存在
     *
     * @param tag
     * @return
     */
    private boolean findTLVIsExist(short tag) {
        if (this.optionalParameters != null && this.optionalParameters.size() != 0) {
            for (SmgpTLV tlv : this.optionalParameters) {
                if (tlv.getTag() == tag) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取 TLV 参数长度
     *
     * @return
     */
    protected int getTLVLength() {
        int length = 0;
        if (this.optionalParameters.size() > 0) {
            for (SmgpTLV tlv : optionalParameters) {
                if (tlv.hasValue()) {
                    length += 4 + tlv.getLength();
                }
            }
        }
        return length;
    }

    @Override
    public void setSendTimeStamp(long timeStamp) {
        this.sendTimestamp = timeStamp;
    }

    @Override
    public long getSendTimeStamp() {
        return sendTimestamp;
    }
}