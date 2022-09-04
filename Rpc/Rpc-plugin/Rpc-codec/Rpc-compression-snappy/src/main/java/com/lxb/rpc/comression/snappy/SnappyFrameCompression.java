package com.lxb.rpc.comression.snappy;




import com.lxb.extension.Extension;
import com.lxb.rpc.codec.compression.Compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Snappy压缩，没有计算校验和
 */
@Extension(value = "snappyf", provider = "pure", order = Compression.SNAPPY_FRAME_ORDER)
public class SnappyFrameCompression implements Compression {
    @Override
    public byte getTypeId() {
        return SNAPPY_FRAME;
    }

    @Override
    public OutputStream compress(final OutputStream out) throws IOException {
        return new SnappyFramedOutputStream(out);
    }

    @Override
    public InputStream decompress(final InputStream input) throws IOException {
        return new SnappyFramedInputStream(input);
    }

    @Override
    public String getTypeName() {
        return "snappyf";
    }
}
