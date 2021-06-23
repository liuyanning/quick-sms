package com.drondea.sms.handler.transcoder;

import com.drondea.sms.common.util.SgipSequenceNumber;
import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.sgip12.AbstractSgipMessage;
import com.drondea.sms.message.sgip12.SgipHeader;
import com.drondea.sms.message.sgip12.SgipPackageType;
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
 * @description: sgip的编解码器
 * @author: liyuehai
 * @date: 2020年06月05日15:24
 **/
@ChannelHandler.Sharable
public class Sgip12MessageCodec extends MessageToMessageCodec<ByteBuf, AbstractSgipMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Sgip12MessageCodec.class);

    private static class SgipMessageCodecHolder {
        private final static Sgip12MessageCodec instance = new Sgip12MessageCodec();
    }

    private ConcurrentHashMap<Integer, ICodec> codecMap = new ConcurrentHashMap<>(16);

    private Sgip12MessageCodec() {
        for (SgipPackageType packetType : SgipPackageType.values()) {
            codecMap.put(packetType.getCommandId(), packetType.getCodec());
        }
    }

    public static Sgip12MessageCodec getInstance() {
        return SgipMessageCodecHolder.instance;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        logger.debug("decode the msg");
        //包长度
        int totalLength = bytebuf.readInt();
        //包类型
        int commandId = bytebuf.readInt();
        long nodeId = bytebuf.readUnsignedInt();
        int timestamp = bytebuf.readInt();
        int sequenceId = bytebuf.readInt();

//        if (sequenceId == 0) {
//            logger.error("sequenceId is null,please set sequenceId.");
//        }
        SgipSequenceNumber sequenceNumber = SgipSequenceNumber.wrapSequenceNumber(nodeId, timestamp, sequenceId);
        SgipHeader header = new SgipHeader(totalLength, commandId, sequenceNumber);
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        if (codec == null) {
            logger.error("can not find commandId {}", commandId);
            return;
        }
        AbstractSgipMessage message = (AbstractSgipMessage) codec.decode(header, bytebuf);
        out.add(message);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractSgipMessage msg, List<Object> out) throws Exception {

        logger.debug("encode the msg");
        SgipHeader header = msg.getHeader();
        if (header == null) {
            logger.error("please set header");
            return;
        }

        //消息总长度
        int totalLength = msg.getBodyLength() + msg.getHeaderLength();

        ByteBuf bodyBuffer = ctx.alloc().buffer(totalLength);
        bodyBuffer.writeInt(totalLength);
        int commandId = header.getCommandId();
        bodyBuffer.writeInt(commandId);
        bodyBuffer.writeInt((int)header.getSequenceNumber().getNodeId());
        bodyBuffer.writeInt(header.getSequenceNumber().getTimestamp());
        bodyBuffer.writeInt(header.getSequenceNumber().getSequenceId());
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        codec.encode(msg, bodyBuffer);
        out.add(bodyBuffer);
    }
}
