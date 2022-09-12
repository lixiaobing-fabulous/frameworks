package io.protostuff;


import com.lxb.rpc.codec.serialization.ObjectWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Protostuff写入器
 */
public abstract class AbstractProtostuffWriter implements ObjectWriter {

    protected Schema       schema;
    protected OutputStream outputStream;
    protected Output       output;
    protected WriteSession session;
    protected WriteSink    sink;

    /**
     * 构造函数
     *
     * @param schema
     * @param output
     * @param session
     * @param outputStream
     */
    protected AbstractProtostuffWriter(Schema schema, Output output, WriteSession session, OutputStream outputStream) {
        this.schema = schema;
        this.outputStream = outputStream;
        this.output = output;
        this.session = session;
        this.sink = session.sink;
    }

    @Override
    public void writeObject(final Object obj) throws IOException {
        schema.writeTo(output, obj);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        session.tail = sink.writeByteArray(b, off, len, session, session.tail);
    }

    @Override
    public void flush() throws IOException {
        LinkedBuffer.writeTo(outputStream, session.head);
    }

    @Override
    public void close() throws IOException {
        release();
        outputStream.close();
    }

    @Override
    public void release() {
        session.clear();
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        session.tail = sink.writeVarInt32(v ? 1 : 0, session, session.tail);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        session.tail = sink.writeByte((byte) v, session, session.tail);
    }

    @Override
    public void writeShort(final int v) throws IOException {
        session.tail = sink.writeVarInt32(v, session, session.tail);
    }

    @Override
    public void writeChar(final int v) throws IOException {
        session.tail = sink.writeVarInt32(v, session, session.tail);
    }

    @Override
    public void writeInt(final int v) throws IOException {
        session.tail = sink.writeVarInt32(v, session, session.tail);
    }

    @Override
    public void writeLong(final long v) throws IOException {
        session.tail = sink.writeVarInt64(v, session, session.tail);
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        session.tail = sink.writeFloat(v, session, session.tail);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        session.tail = sink.writeDouble(v, session, session.tail);
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        session.tail = sink.writeStrUTF8(s, session, session.tail);
    }

}
