package com.lxb.rpc.comression.snappy;


import com.lxb.extension.Extension;
import com.lxb.rpc.codec.compression.Compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Extension(value = "snappy", provider = "pure", order = Compression.SNAPPY_ORDER)
public class SnappyCompression implements Compression {

    @Override
    public OutputStream compress(OutputStream out) throws IOException {
        return new SnappyOutputStream(out);
    }

    @Override
    public InputStream decompress(InputStream input) throws IOException {
        return new SnappyInputStream(input);
    }

    @Override
    public byte getTypeId() {
        return SNAPPY;
    }

    @Override
    public String getTypeName() {
        return "snappy";
    }
}
