package com.lxb.rpc.serialization.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.lxb.rpc.codec.serialization.ObjectReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Kryo数据读入器
 */
public class KryoReader implements ObjectReader {

    protected Kryo kryo;

    protected Input input;

    public KryoReader(Kryo kryo, Input input) {
        this.kryo = kryo;
        this.input = input;
    }

    @Override
    public <T> T readObject(final Class<T> clazz) throws IOException {
        try {
            return (T) kryo.readClassAndObject(input);
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public Object readObject() throws IOException {
        try {
            return kryo.readClassAndObject(input);
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public String readString(final Charset charset, final boolean shortLength) throws IOException {
        try {
            return input.readString();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int read() throws IOException {
        try {
            return input.read();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int read(final byte[] b) throws IOException {
        try {
            return input.read(b);
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return input.read(b, off, len);
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return input.available();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            input.close();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return input.readBoolean();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return input.readByte();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return input.readByteUnsigned();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            return input.readShort();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        try {
            return input.readShortUnsigned();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public char readChar() throws IOException {
        try {
            return input.readChar();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return input.readInt();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return input.readLong();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return input.readFloat();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return input.readDouble();
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long skip(final long n) throws IOException {
        try {
            return input.skip(n);
        } catch (KryoException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
