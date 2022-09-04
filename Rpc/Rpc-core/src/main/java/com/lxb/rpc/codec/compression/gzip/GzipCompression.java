package com.lxb.rpc.codec.compression.gzip;



import com.lxb.extension.Extension;
import com.lxb.rpc.codec.compression.Compression;
import com.lxb.rpc.codec.compression.Finishable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip压缩算法
 */
@Extension(value = "gzip", provider = "java")
public class GzipCompression implements Compression {

    @Override
    public byte getTypeId() {
        return GZIP;
    }

    @Override
    public String getTypeName() {
        return "gzip";
    }

    @Override
    public OutputStream compress(final OutputStream out) throws IOException {
        return new MyGZIPOutputStream(out);
    }

    @Override
    public InputStream decompress(final InputStream input) throws IOException {
        return new GZIPInputStream(input);
    }

    /**
     * 压缩
     */
    protected static class MyGZIPOutputStream extends GZIPOutputStream implements Finishable {

        public MyGZIPOutputStream(final OutputStream out) throws IOException {
            super(out);
        }

    }
}
