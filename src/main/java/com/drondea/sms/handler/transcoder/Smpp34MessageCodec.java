package com.drondea.sms.handler.transcoder;

import com.drondea.sms.common.util.SmppUtil;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.smpp34.AbstractSmppMessage;
import com.drondea.sms.message.smpp34.SmppHeader;
import com.drondea.sms.message.smpp34.SmppPackageType;
import com.drondea.sms.message.smpp34.Tlv;
import com.drondea.sms.type.InvalidCommandIdException;
import com.drondea.sms.type.SmppConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.drondea.sms.common.util.SmppUtil.readTlv;

/**
 * @version V3.0.0
 * @description: smpp的编解码入口
 * @author: 刘彦宁
 * @date: 2020年07月14日11:34
 **/
@ChannelHandler.Sharable
public class Smpp34MessageCodec extends MessageToMessageCodec<ByteBuf, AbstractSmppMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Smpp34MessageCodec.class);

    private static class SmppMessageCodecHolder {
        private final static Smpp34MessageCodec instance = new Smpp34MessageCodec();
    }

    private ConcurrentHashMap<Integer, ICodec> codecMap = new ConcurrentHashMap<>(16);

    private Smpp34MessageCodec() {
        for (SmppPackageType packetType : SmppPackageType.values()) {
            codecMap.put(packetType.getCommandId(), packetType.getCodec());
        }
    }

    public static Smpp34MessageCodec getInstance() {
        return Smpp34MessageCodec.SmppMessageCodecHolder.instance;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractSmppMessage msg, List<Object> out) throws Exception {
        logger.debug("encode the msg");
        SmppHeader header = msg.getHeader();
        if (header == null) {
            logger.error("please set header");
            return;
        }
        //消息总长度
        int totalLength = msg.getBodyLength() + msg.getHeaderLength() + msg.getTlvLength();
        ByteBuf bodyBuffer = ctx.alloc().buffer(totalLength);
        bodyBuffer.writeInt(totalLength);
        int commandId = header.getCommandId();
        bodyBuffer.writeInt(commandId);
        if (msg.isRequest()) {
            bodyBuffer.writeInt(0);
        } else {
            bodyBuffer.writeInt(header.getCommandStatus());
        }

        bodyBuffer.writeInt(header.getSequenceNumber());
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        if (codec == null) {
            msg.getHeader().setCommandStatus(SmppConstants.STATUS_INVCMDID);
            throw new InvalidCommandIdException("There is no corresponding commandId :" + commandId, header);
        }
        codec.encode(msg, bodyBuffer);
        out.add(bodyBuffer);

        //可选参数
        if (msg.getOptionalParameters() == null) {
            return;
        }
        for (Tlv tlv : msg.getOptionalParameters()) {
            SmppUtil.writeTlv(bodyBuffer, tlv);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        logger.debug("decode the msg");
        int commandLength = bytebuf.readInt();
        int commandId = bytebuf.readInt();
        int commandStatus = bytebuf.readInt();
        int sequenceNumber = bytebuf.readInt();
        SmppHeader header = new SmppHeader(commandLength, commandId, commandStatus, sequenceNumber);
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        if (codec == null) {
            logger.error("can not find commandId {}", commandId);
            return;
        }
        AbstractSmppMessage message = (AbstractSmppMessage) codec.decode(header, bytebuf);
        if (message == null) {
            return;
        }
        out.add(message);
        //可选参数
        while (bytebuf.readableBytes() > 0) {
            Tlv tlv = readTlv(bytebuf);
            message.addOptionalParameter(tlv);
        }
    }
}
