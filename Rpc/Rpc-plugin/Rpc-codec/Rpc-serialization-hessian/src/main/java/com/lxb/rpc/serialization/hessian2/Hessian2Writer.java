package com.lxb.rpc.serialization.hessian2;



import com.lxb.rpc.codec.serialization.ObjectWriter;
import hessian.io.AbstractHessianOutput;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * HessianLite 输出
 */
public class Hessian2Writer implements ObjectWriter {
    /**
     * hessian2输出
     */
    protected final AbstractHessianOutput hessian2Output;

    public Hessian2Writer(AbstractHessianOutput hessian2Output) {
        this.hessian2Output = hessian2Output;
    }

    @Override
    public void writeObject(final Object obj) throws IOException {
        hessian2Output.writeObject(obj);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        hessian2Output.writeBytes(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        hessian2Output.flush();
    }

    @Override
    public void close() throws IOException {
        hessian2Output.close();
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        hessian2Output.writeBoolean(v);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        hessian2Output.writeInt(v);
    }

    @Override
    public void writeShort(final int v) throws IOException {
        hessian2Output.writeInt(v);
    }

    @Override
    public void writeChar(final int v) throws IOException {
        hessian2Output.writeInt(v);
    }

    @Override
    public void writeInt(final int v) throws IOException {
        hessian2Output.writeInt(v);
    }

    @Override
    public void writeLong(final long v) throws IOException {
        hessian2Output.writeLong(v);
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        hessian2Output.writeDouble(v);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        hessian2Output.writeDouble(v);
    }

    @Override
    public void writeString(final String value, final Charset charset, final boolean zeroNull, final boolean shortLength) throws IOException {
        hessian2Output.writeString(value);
    }
}
