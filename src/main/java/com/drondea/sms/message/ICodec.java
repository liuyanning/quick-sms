package com.drondea.sms.message;

import io.netty.buffer.ByteBuf;

/**
 * @author liuyanning
 */
public interface ICodec {

    /**
     * 解码
     *
     * @param header
     * @param bodyBuffer
     * @return
     */
    IMessage decode(IHeader header, ByteBuf bodyBuffer) throws Exception;

    /**
     * 编码
     *
     * @param msg
     * @param bodyBuffer
     * @return
     */
    ByteBuf encode(IMessage msg, ByteBuf bodyBuffer) throws Exception;
}
