package coompress;

import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.rpc.codec.UnsafeByteArrayOutputStream;
import com.lxb.rpc.codec.compression.AdaptiveCompressOutputStream;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;
import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CompressTest {
    ExtensionPoint<Compression, String> COMPRESSION = new ExtensionPointLazy<>(Compression.class);

    @Test
    public void testCompression() throws IOException {
        List<String> types  = COMPRESSION.names();
        byte[]       source = new byte[10];
        source[1] = 1;
        byte[] target = new byte[10];

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (String type : types) {
            try {
                bos.reset();
                Compression  compression = COMPRESSION.get(type);
                OutputStream os          = compression.compress(bos);
                os.write(source);
                if (os instanceof Finishable) {
                    ((Finishable) os).finish();
                }
                os.flush();
                byte[]               bytes = bos.toByteArray();
                ByteArrayInputStream bis   = new ByteArrayInputStream(bytes);
                InputStream          is    = compression.decompress(bis);
                is.read(target);
                Assertions.assertArrayEquals(source, target);
            } catch (Throwable e) {
                System.out.println("compress error " + type);
                throw e;
            }
        }

    }

    @Test
    public void testTps() throws IOException {

        List<String> types = COMPRESSION.names();
        //LZMA太慢了，去掉性能测试
        types.remove("lzma");

        byte[] source = new byte[2048];
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) (i % 128);
        }
        byte[] target = new byte[2048];

        UnsafeByteArrayOutputStream baos = new UnsafeByteArrayOutputStream(1024);
        long                        startTime;
        long endTime;
        long encodeTime;
        long decodeTime;
        long size;
        long count = 10000;
        Compression compression;
        for (String type : types) {
            compression = COMPRESSION.get(type);
            encodeTime = 0;
            decodeTime = 0;
            size = 0;
            for (int i = 0; i < count; i++) {
                baos.reset();
                startTime = System.nanoTime();
                OutputStream os = compression.compress(baos);
                os.write(source);
                if (os instanceof Finishable) {
                    ((Finishable) os).finish();
                }
                os.flush();
                endTime = System.nanoTime();
                encodeTime += endTime - startTime;
                size += baos.size();
                ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
                startTime = System.nanoTime();
                InputStream is = compression.decompress(bis);
                is.read(target);
                endTime = System.nanoTime();
                decodeTime += endTime - startTime;
            }
            System.out.println(String.format("%s encode_tps %d decode_tps %d size %d", type, count * 1000000000L / encodeTime, count * 1000000000L / decodeTime, size / count));
        }
    }

    @Test
    public void testAdaptive() throws IOException {

        Compression                  lz4  = COMPRESSION.get("lz4");
        AdaptiveCompressOutputStream acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[120]);
        acos.finish();
        Assertions.assertFalse(acos.isCompressed());

        acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[127]);
        acos.finish();
        Assertions.assertFalse(acos.isCompressed());

        acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[128]);
        acos.finish();
        Assertions.assertTrue(acos.isCompressed());

        acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[127]);
        acos.write(1);
        acos.finish();
        Assertions.assertTrue(acos.isCompressed());

        acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[100]);
        acos.write(new byte[27]);
        acos.finish();
        Assertions.assertFalse(acos.isCompressed());

        acos = new AdaptiveCompressOutputStream(new NettyChannelBuffer(ByteBufAllocator.DEFAULT.buffer(1024)), lz4, 128);
        acos.write(1);
        acos.write(new byte[100]);
        acos.write(new byte[28]);
        acos.finish();
        Assertions.assertTrue(acos.isCompressed());

    }
}
