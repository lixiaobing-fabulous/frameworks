package com.lxb.rpc.serialization.hessian2;


import com.lxb.rpc.codec.serialization.ObjectReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * HessianLite输入
 */
public class Hessian2Reader implements ObjectReader {

    protected final Hessian2BWLInput hessian2Input;

    public Hessian2Reader(Hessian2BWLInput hessian2Input) {
        this.hessian2Input = hessian2Input;
    }

    @Override
    public String readString(final Charset charset, final boolean shortLength) throws IOException {
        return hessian2Input.readString();
    }

    @Override
    public Object readObject() throws IOException {
        return hessian2Input.readObject();
    }

    @Override
    public <T> T readObject(Class<T> clazz) throws IOException {
        return (T) hessian2Input.readObject(clazz);
    }

    @Override
    public int read() throws IOException {
        return hessian2Input.read();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return hessian2Input.readBytes(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return hessian2Input.readBytes(b, off, len);
    }

    @Override
    public int available() throws IOException {
        return hessian2Input.available();
    }

    @Override
    public void close() throws IOException {
        hessian2Input.close();
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        hessian2Input.readBytes(b, off, len);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return hessian2Input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) hessian2Input.readInt();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return hessian2Input.readInt() & 0xFFFFFFFF;
    }

    @Override
    public short readShort() throws IOException {
        return (short) hessian2Input.readInt();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return hessian2Input.readInt() & 0xFFFFFFFF;
    }

    @Override
    public char readChar() throws IOException {
        return (char) hessian2Input.readInt();
    }

    @Override
    public int readInt() throws IOException {
        return hessian2Input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return hessian2Input.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return hessian2Input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return hessian2Input.readDouble();
    }
}
