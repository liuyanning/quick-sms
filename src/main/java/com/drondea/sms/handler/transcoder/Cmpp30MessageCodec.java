package com.drondea.sms.handler.transcoder;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.cmpp.CmppHeader;
import com.drondea.sms.message.cmpp.AbstractCmppMessage;
import com.drondea.sms.message.cmpp30.CmppPackageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version V3.0.0
 * @description: cmpp的编解码器
 * @author: 刘彦宁
 * @date: 2020年06月05日15:24
 **/
@ChannelHandler.Sharable
public class Cmpp30MessageCodec extends MessageToMessageCodec<ByteBuf, AbstractCmppMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Cmpp30MessageCodec.class);

    private static class CmppMessageCodecHolder {
        private final static Cmpp30MessageCodec instance = new Cmpp30MessageCodec();
    }

    private ConcurrentHashMap<Integer, ICodec> codecMap = new ConcurrentHashMap<>(16);

    private Cmpp30MessageCodec() {
        for (CmppPackageType packetType : CmppPackageType.values()) {
            codecMap.put(packetType.getCommandId(), packetType.getCodec());
        }
    }

    public static Cmpp30MessageCodec getInstance() {
        return CmppMessageCodecHolder.instance;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractCmppMessage msg, List<Object> out) throws Exception {

        logger.debug("encode the msg");
        CmppHeader header = msg.getHeader();
        if (header == null) {
            logger.error("please set header");
            return;
        }
        //消息总长度
        int totalLength = msg.getBodyLength30() + msg.getHeaderLength();
        ByteBuf bodyBuffer = ctx.alloc().buffer(totalLength);
        bodyBuffer.writeInt(totalLength);
        int commandId = header.getCommandId();
        bodyBuffer.writeInt(commandId);
        bodyBuffer.writeInt(header.getSequenceId());
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        codec.encode(msg, bodyBuffer);
        out.add(bodyBuffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        logger.debug("decode the msg");
        //包长度
        int totalLength = bytebuf.readInt();
        //包类型
        int commandId = bytebuf.readInt();
        int sequenceId = bytebuf.readInt();

        if (sequenceId == 0) {
            logger.error("sequenceId is null,please set sequenceId.");
        }
        CmppHeader header = new CmppHeader(totalLength, commandId, sequenceId);
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        if (codec == null) {
            logger.error("can not find commandId {}", commandId);
            return;
        }
        AbstractCmppMessage message = (AbstractCmppMessage) codec.decode(header, bytebuf);
        out.add(message);
    }
}
