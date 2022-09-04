package com.lxb.rpc.codec;

import com.lxb.extension.ExtensionManager;
import com.lxb.extension.ExtensionPoint;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;


public class MessageEncoder extends MessageToByteEncoder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        ExtensionPoint<Serialization, Object> extensionPoint        = ExtensionManager.getOrLoadExtensionPoint(Serialization.class);
        ByteArrayOutputStream                 byteArrayOutputStream = new ByteArrayOutputStream();
        extensionPoint.get("java@advance").getSerializer().serialize(byteArrayOutputStream, message);
        byte[] data = byteArrayOutputStream.toByteArray();

        ExtensionPoint<Compression, Object> compressionExtensionPoint = ExtensionManager.getOrLoadExtensionPoint(Compression.class);
        byte[]                              compress                  = compressionExtensionPoint.get("zlib").compress(data);
        out.writeInt(compress.length);
        out.writeBytes(compress);
        logger.info("Encode {} to bytes[length:{}]", message, data.length);
    }
}
