package com.lxb.rpc.transport.buffer;



import com.lxb.rpc.codec.ArrayInputStream;

import java.io.InputStream;

public class ChannelBufferInputStream extends InputStream implements ArrayInputStream {
    //缓冲区
    protected ChannelBuffer buffer;
    //最大位置
    protected int           endIndex;

    public ChannelBufferInputStream(ChannelBuffer buffer) {
        this(buffer, buffer.readableBytes());
    }

    public ChannelBufferInputStream(ChannelBuffer buffer, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length > buffer.readableBytes()) {
            throw new IndexOutOfBoundsException();
        }
        this.buffer = buffer;
        this.endIndex = buffer.readerIndex() + length;
    }

    @Override
    public int read() {
        return buffer.isReadable() ? buffer.readByte() & 0xff : -1;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) {
        int available = available();
        if (available <= 0) {
            return -1;
        }
        int length = Math.min(available, len);
        buffer.readBytes(b, off, length);
        return length;
    }

    @Override
    public int available() {
        return endIndex - buffer.readerIndex();
    }

    @Override
    public boolean hasArray() {
        return buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return buffer.array();
    }

    @Override
    public int arrayOffset() {
        return buffer.arrayOffset();
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }
}
