package com.lxb.rpc.codec.serialization;


import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Objects;

/**
 * ObjectOutput写入器
 */
public class ObjectOutputWriter implements ObjectWriter {

    protected final ObjectOutput output;

    public ObjectOutputWriter(final ObjectOutput output) {
        Objects.requireNonNull(output);
        this.output = output;
    }

    @Override
    public void writeObject(final Object v) throws IOException {
        output.writeObject(v);
    }

    @Override
    public void write(final int b) throws IOException {
        //java的内置write，会block直到写入
        output.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (b != null) {
            output.write(b, off, len);
        }
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        output.writeByte(v);
    }

    @Override
    public void writeShort(final int v) throws IOException {
        output.writeShort(v);
    }

    @Override
    public void writeChar(final int v) throws IOException {
        output.writeChar(v);
    }

    @Override
    public void writeInt(final int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeLong(final long v) throws IOException {
        output.writeLong(v);
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        output.writeFloat(v);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        output.writeDouble(v);
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        //保持和原有一样
        output.writeBytes(s);
    }

    @Override
    public void writeChars(final String s) throws IOException {
        output.writeChars(s);
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        //保持和原有一样
        output.writeUTF(s);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }

}
