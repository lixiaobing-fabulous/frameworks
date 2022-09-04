package com.lxb.rpc.compression.lz4;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Lz4压缩算法
 */
@Extension(value = "lz4f", provider = "commons-compress", order = Compression.LZ4_FRAME_ORDER)
@ConditionalOnClass("org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream")
public class Lz4FrameCompression implements Compression {
    @Override
    public byte getTypeId() {
        return LZ4_FRAME;
    }

    @Override
    public OutputStream compress(final OutputStream out) throws IOException {
        return new MyFramedLZ4CompressorOutputStream(out);
    }

    @Override
    public InputStream decompress(final InputStream input) throws IOException {
        return new FramedLZ4CompressorInputStream(input);
    }

    @Override
    public String getTypeName() {
        return "lz4f";
    }

    /**
     * 覆盖flush操作
     */
    protected static class MyFramedLZ4CompressorOutputStream extends FramedLZ4CompressorOutputStream implements Finishable {

        protected OutputStream out;

        public MyFramedLZ4CompressorOutputStream(OutputStream out) throws IOException {
            super(out, new Parameters(BlockSize.K64, false, false, false));
            this.out = out;
        }

        public MyFramedLZ4CompressorOutputStream(OutputStream out, Parameters params) throws IOException {
            super(out, params);
            this.out = out;
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }
    }
}
