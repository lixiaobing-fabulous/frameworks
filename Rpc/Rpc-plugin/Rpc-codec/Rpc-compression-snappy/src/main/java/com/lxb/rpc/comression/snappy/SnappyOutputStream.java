package com.lxb.rpc.comression.snappy;


import com.lxb.rpc.codec.compression.Finishable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SnappyOutputStream extends ByteArrayOutputStream implements Finishable {

    protected OutputStream output;
    protected boolean      finished;
    protected boolean      closed;

    /**
     * 构造函数
     *
     * @param outputStream
     */
    public SnappyOutputStream(OutputStream outputStream) {
        super(1024);
        this.output = outputStream;
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void finish() throws IOException {
        if (!finished) {
            byte[] compressedOut  = new byte[SnappyCompressor.maxCompressedLength(buf.length)];
            int    compressedSize = SnappyCompressor.compress(buf, 0, count, compressedOut, 0);
            output.write(compressedOut, 0, compressedSize);
            finished = true;
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            try {
                finish();
            } finally {
                super.close();
                output.close();
            }
        }
    }

}
