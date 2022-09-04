package com.lxb.rpc.compression.lzma;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Lzma算法
 */
@Extension(value = "lzma", provider = "tukaani", order = Compression.LZMA_ORDER)
@ConditionalOnClass("org.tukaani.xz.LZMAOutputStream")
public class LzmaCompression implements Compression {
    @Override
    public byte getTypeId() {
        return LZMA;
    }

    @Override
    public OutputStream compress(final OutputStream out) throws IOException {
        return new MyLZMAOutputStream(out);
    }

    @Override
    public InputStream decompress(final InputStream input) throws IOException {
        return new LZMAInputStream(input, -1);
    }

    @Override
    public String getTypeName() {
        return "lzma";
    }

    /**
     * 覆盖flush操作
     */
    protected static class MyLZMAOutputStream extends LZMAOutputStream implements Finishable {

        protected OutputStream os;

        public MyLZMAOutputStream(OutputStream os) throws IOException {
            super(os, new LZMA2Options(), -1);
            this.os = os;
        }

        @Override
        public void flush() throws IOException {
            os.flush();
        }
    }
}
