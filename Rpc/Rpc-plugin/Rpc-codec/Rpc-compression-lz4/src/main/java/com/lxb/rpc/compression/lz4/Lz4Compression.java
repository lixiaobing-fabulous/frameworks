package com.lxb.rpc.compression.lz4;

import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.lxb.rpc.codec.compression.Compression.LZ4;

/**
 * Lz4压缩算法
 */
@Extension(value = "lz4", provider = "commons-compress", order = Compression.LZ4_ORDER)
@ConditionalOnClass("org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream")
public class Lz4Compression implements Compression {
    @Override
    public byte getTypeId() {
        return LZ4;
    }

    @Override
    public OutputStream compress(OutputStream out) throws IOException {
        return new MyBlockLZ4CompressorOutputStream(out);
    }

    @Override
    public InputStream decompress(InputStream input) throws IOException {
        return new BlockLZ4CompressorInputStream(input);
    }

    @Override
    public String getTypeName() {
        return "lz4";
    }

    /**
     * 覆盖flush操作
     */
    protected static class MyBlockLZ4CompressorOutputStream extends BlockLZ4CompressorOutputStream implements Finishable {

        protected final OutputStream os;

        public MyBlockLZ4CompressorOutputStream(OutputStream os) throws IOException {
            super(os);
            this.os = os;
        }

        public MyBlockLZ4CompressorOutputStream(OutputStream os, Parameters params) throws IOException {
            super(os, params);
            this.os = os;
        }

        @Override
        public void flush() throws IOException {
            os.flush();
        }
    }
}
