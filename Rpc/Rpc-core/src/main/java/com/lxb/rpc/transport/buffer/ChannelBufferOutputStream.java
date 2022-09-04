package com.lxb.rpc.transport.buffer;


import java.io.OutputStream;

public class ChannelBufferOutputStream extends OutputStream {

    protected ChannelBuffer buffer;

    public ChannelBufferOutputStream(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(final int b) {
        buffer.writeByte(b);
    }

    @Override
    public void write(final byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        if (length <= 0) {
            return;
        }
        buffer.writeBytes(bytes, offset, length);
    }

}
