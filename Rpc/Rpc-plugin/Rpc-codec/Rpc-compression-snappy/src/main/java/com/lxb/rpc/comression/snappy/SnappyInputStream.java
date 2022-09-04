package com.lxb.rpc.comression.snappy;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SnappyInputStream extends ByteArrayInputStream {

    public SnappyInputStream(InputStream inputStream) throws IOException {
        super(new byte[0]);
        int size = inputStream.available();
        if (size > 0) {
            byte[] source = new byte[size];
            inputStream.read(source);
            this.buf = SnappyDecompressor.uncompress(source, 0, source.length);
            this.pos = 0;
            this.count = buf.length;
        }
    }

}
