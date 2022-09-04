package com.lxb.rpc.codec.compression.zlib;

import com.lxb.extension.Extension;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Zlib压缩算法
 */
@Extension(value = "zlib", provider = "java", order = Compression.ZLIB_ORDER)
public class ZlibCompression implements Compression {

    @Override
    public byte getTypeId() {
        return ZLIB;
    }

    @Override
    public String getTypeName() {
        return "zlib";
    }

    @Override
    public OutputStream compress(final OutputStream out) {
        return new MyDeflaterOutputStream(out);
    }

    @Override
    public InputStream decompress(final InputStream input) {
        return new InflaterInputStream(input);
    }

    /**
     * 压缩
     */
    protected static class MyDeflaterOutputStream extends DeflaterOutputStream implements Finishable {

        public MyDeflaterOutputStream(OutputStream out) {
            super(out);
        }
    }
}
