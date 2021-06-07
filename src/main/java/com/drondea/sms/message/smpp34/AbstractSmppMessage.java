package com.drondea.sms.message.smpp34;

import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.IPackageType;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.IMessageResponseHandler;
import com.drondea.sms.type.SmppConstants;

import java.util.ArrayList;

/**
 * @version V3.0.0
 * @description: smpp包父类
 * @author: gengjinbiao
 * @date: 2020年07月06日14:23
 **/
public abstract class AbstractSmppMessage implements IMessage, Cloneable {

    private IPackageType packageType;
    private long timestamp;
    /**
     * 消息的生命周期，单位秒, 0表示永不过期
     */
    private long lifeTime = 0;
    private SmppHeader header;

    private ArrayList<Tlv> optionalParameters;

    /**
     * 发送重试次数
     */
    private int retryCount = 0;
    private IMessageResponseHandler messageResponseHandler;
    private IMessage requestMessage;
    private long sendTimestamp;

    public AbstractSmppMessage(IPackageType packageType) {
        setPackageType(packageType);
        SmppHeader header = new SmppHeader(packageType);
        header.setCommandId(packageType.getCommandId());
        setHeader(header);
    }

    public AbstractSmppMessage(IPackageType packageType, SmppHeader header) {
        setPackageType(packageType);
        if (header == null) {
            header = new SmppHeader(packageType);

            header.setSequenceNumber(GlobalConstants.sequenceNumber.next());
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

    public SmppHeader getHeader() {
        return header;
    }

    public void setHeader(SmppHeader header) {
        this.header = header;
    }

    public int getHeaderLength() {
        return SmppConstants.PDU_HEADER_LENGTH;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
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
     * 获取消息的body长度
     *
     * @return
     */
    public abstract int getBodyLength();


    @Override
    public int getSequenceId() {
        return getHeader().getSequenceNumber();
    }

    /**
     * 把header也拷贝一份，本身clone是浅拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public AbstractSmppMessage clone() throws CloneNotSupportedException {
        AbstractSmppMessage abstractSmppMessage = (AbstractSmppMessage) super.clone();
        SmppHeader cmppHeader = this.getHeader().clone();
        abstractSmppMessage.setHeader(cmppHeader);
        return abstractSmppMessage;
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
        return false;
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
    public void handleMessageExpired(String cachedKey,IMessage request) {
        IMessageResponseHandler messageCompleteHandler = getMessageResponseHandler();
        if (messageCompleteHandler != null) {
            messageCompleteHandler.messageExpired(cachedKey, request);
        }
    }

    public int getOptionalParameterCount() {
        if (this.optionalParameters == null) {
            return 0;
        }
        return this.optionalParameters.size();
    }

    public ArrayList<Tlv> getOptionalParameters() {
        return this.optionalParameters;
    }

    public void addOptionalParameter(Tlv tlv) {
        if (this.optionalParameters == null) {
            this.optionalParameters = new ArrayList<Tlv>();
        }
        this.optionalParameters.add(tlv);
    }

    public Tlv setOptionalParameter(Tlv tlv) {
        // does this parameter already exist?
        int i = this.findOptionalParameter(tlv.getTag());
        if (i < 0) {
            // parameter does not yet exist, add it, not replaced
            this.addOptionalParameter(tlv);
            return null;
        } else {
            // this parameter already exists, replace it, return old
            return this.optionalParameters.set(i, tlv);
        }
    }

    public boolean hasOptionalParameter(short tag) {
        return (this.findOptionalParameter(tag) >= 0);
    }

    protected int findOptionalParameter(short tag) {
        if (this.optionalParameters == null) {
            return -1;
        }
        int i = 0;
        for (Tlv tlv : this.optionalParameters) {
            if (tlv.getTag() == tag) {
                return i;
            }
            i++;
        }
        // if we get here, we didn't find the parameter by tag
        return -1;
    }

    public Tlv getOptionalParameter(short tag) {
        if (this.optionalParameters == null) {
            return null;
        }
        // try to find this parameter's index
        int i = this.findOptionalParameter(tag);
        if (i < 0) {
            return null;
        }
        return this.optionalParameters.get(i);
    }

    public int getTlvLength() {
        if (this.optionalParameters == null) {
            return 0;
        }
        int optParamLength = 0;
        // otherwise, add length of each tlv
        for (Tlv tlv : this.optionalParameters) {
            optParamLength += tlv.calculateByteSize();
        }
        return optParamLength;
    }

//    public void readOptionalParameters(ChannelBuffer buffer, PduTranscoderContext context) throws UnrecoverablePduException, RecoverablePduException {
//        // if there is any data left, it's part of an optional parameter
//        while (buffer.readableBytes() > 0) {
//            Tlv tlv = ChannelBufferUtil.readTlv(buffer);
//            if (tlv.getTagName() == null) {
//                tlv.setTagName(context.lookupTlvTagName(tlv.getTag()));
//            }
//            this.addOptionalParameter(tlv);
//        }
//    }
//
//    public void writeOptionalParameters(ChannelBuffer buffer, PduTranscoderContext context) throws UnrecoverablePduException, RecoverablePduException {
//        if (this.optionalParameters == null) {
//            return;
//        }
//        for (Tlv tlv : this.optionalParameters) {
//            if (tlv.getTagName() == null) {
//                tlv.setTagName(context.lookupTlvTagName(tlv.getTag()));
//            }
//            ChannelBufferUtil.writeTlv(buffer, tlv);
//        }
//    }

    protected void appendOptionalParameterToString(StringBuilder buffer) {
        if (this.optionalParameters == null) {
            return;
        }
        int i = 0;
        for (Tlv tlv : this.optionalParameters) {
            if (i != 0) {
                buffer.append(" (");
            } else {
                buffer.append("(");
            }
            // format 0x0000 0x0000 [00..]
            buffer.append(tlv.toString());
            buffer.append(")");
            i++;
        }
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
