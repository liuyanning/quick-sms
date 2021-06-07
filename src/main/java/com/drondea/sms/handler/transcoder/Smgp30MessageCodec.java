package com.drondea.sms.handler.transcoder;

import com.drondea.sms.message.ICodec;
import com.drondea.sms.message.smgp30.msg.AbstractSmgpMessage;
import com.drondea.sms.message.smgp30.msg.SmgpHeader;
import com.drondea.sms.message.smgp30.msg.SmgpPackageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * @version V3.0
 * @description: smgp的编解码器
 * @author: ywj
 * @date: 2020年06月05日15:24
 **/
@ChannelHandler.Sharable
public class Smgp30MessageCodec extends MessageToMessageCodec<ByteBuf, AbstractSmgpMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Smgp30MessageCodec.class);

    private static class SmgpMessageCodecHolder {
        private final static Smgp30MessageCodec instance = new Smgp30MessageCodec();
    }

    private ConcurrentHashMap<Integer, ICodec> codecMap = new ConcurrentHashMap<>(16);

    private Smgp30MessageCodec() {
        for (SmgpPackageType packetType : SmgpPackageType.values()) {
            codecMap.put(packetType.getCommandId(), packetType.getCodec());
        }
    }

    public static Smgp30MessageCodec getInstance() {
        return SmgpMessageCodecHolder.instance;
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
        SmgpHeader header = new SmgpHeader(totalLength, commandId, sequenceId);
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        if (codec == null) {
            logger.error("can not find commandId {}", commandId);
            return;
        }
        AbstractSmgpMessage message = (AbstractSmgpMessage) codec.decode(header, bytebuf);
        out.add(message);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractSmgpMessage msg, List<Object> out) throws Exception {

        logger.debug("encode the msg");
        SmgpHeader header = msg.getHeader();
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
        bodyBuffer.writeInt(header.getSequenceId());
        //获取具体的编解码器进行处理
        ICodec codec = codecMap.get(commandId);
        codec.encode(msg, bodyBuffer);
        out.add(bodyBuffer);
    }
}
