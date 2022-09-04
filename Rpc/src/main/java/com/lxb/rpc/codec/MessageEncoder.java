package com.lxb.rpc.codec;

import com.lxb.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageEncoder extends MessageToByteEncoder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        Serializer serializer = Serializer.DEFAULT;
        byte[] data = serializer.serialize(message);
        out.writeInt(data.length);
        out.writeBytes(data);
        logger.info("Encode {} to bytes[length:{}]", message, data.length);
    }
}
