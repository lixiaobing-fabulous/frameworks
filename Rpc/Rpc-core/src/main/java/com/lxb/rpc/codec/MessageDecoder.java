package com.lxb.rpc.codec;

import com.lxb.extension.ExtensionManager;
import com.lxb.extension.ExtensionPoint;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        ExtensionPoint<Compression, Object> compressionExtensionPoint = ExtensionManager.getOrLoadExtensionPoint(Compression.class);
        byte[]                              decompress                = compressionExtensionPoint.get("zlib").decompress(data);
        ExtensionPoint<Serialization, Object> extensionPoint       = ExtensionManager.getOrLoadExtensionPoint(Serialization.class);
        Object                                object               = extensionPoint.get("java@advance").getSerializer().deserialize(new ByteArrayInputStream(decompress), Object.class);
        out.add(object);
        log.info("Serialize from bytes[length:{}] to be a {}", dataLength, object);

    }
}
