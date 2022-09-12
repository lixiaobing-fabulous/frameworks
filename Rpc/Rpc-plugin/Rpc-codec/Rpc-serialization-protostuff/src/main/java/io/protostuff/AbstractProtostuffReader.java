package io.protostuff;


import com.lxb.rpc.codec.serialization.ObjectReader;
import io.protostuff.CodedInput;
import io.protostuff.ProtobufException;
import io.protostuff.Schema;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Protostuf读入器
 */
public class AbstractProtostuffReader implements ObjectReader {
    /**
     * Schema
     */
    protected Schema      schema;
    /**
     * 输入流
     */
    protected InputStream inputStream;
    /**
     * 输入
     */
    protected CodedInput  input;

    /**
     * 构造函数
     *
     * @param schema
     * @param inputStream
     * @param input
     */
    public AbstractProtostuffReader(Schema schema, InputStream inputStream, CodedInput input) {
        this.schema = schema;
        this.inputStream = inputStream;
        this.input = input;
    }

    @Override
    public Object readObject() throws IOException {
        Object message = schema.newMessage();
        schema.mergeFrom(input, message);
        input.checkLastTagWas(0);
        return message;
    }

    @Override
    public String readUTF() throws IOException {
        return input.readString();
    }

    @Override
    public int read() throws IOException {
        return input.readInt32();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            return 0;
        }
        int end = off + len;
        if (off < 0 || len < 0 || end > b.length || end < 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < len; i++) {
            try {
                b[i] = input.readRawByte();
            } catch (EOFException e) {
                return i;
            } catch (ProtobufException e) {
                return i;
            } catch (IllegalStateException e) {
                return i;
            }
        }
        return len;
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBool();
    }

    @Override
    public byte readByte() throws IOException {
        return input.readRawByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return input.readRawByte() & 0xFFFFFFFF;
    }

    @Override
    public short readShort() throws IOException {
        return (short) input.readInt32();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return input.readUInt32();
    }

    @Override
    public char readChar() throws IOException {
        return (char) input.readRawVarint32();
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt32();
    }

    @Override
    public long readLong() throws IOException {
        return input.readInt64();
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }
}
