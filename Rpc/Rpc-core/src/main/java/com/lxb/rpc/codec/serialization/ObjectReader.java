package com.lxb.rpc.codec.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface ObjectReader extends ObjectInput {
    String EMPTY_STRING = "";

    @Override
    default int read(final byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        return read(b, 0, b.length);
    }

    @Override
    default void readFully(final byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        readFully(b, 0, b.length);
    }

    @Override
    default void readFully(final byte[] b, final int off, final int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        int count;
        while (n < len) {
            count = read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    @Override
    Object readObject() throws IOException;

    /**
     * 读对象
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    default <T> T readObject(final Class<T> clazz) throws IOException {
        return (T) readObject();
    }

    @Override
    default String readLine() throws IOException {
        return readString(StandardCharsets.UTF_8, true);
    }

    @Override
    default String readUTF() throws IOException {
        return readString(StandardCharsets.UTF_8, true);
    }

    /**
     * 读取字符串
     *
     * @return
     * @throws IOException
     */
    default String readString() throws IOException {
        return readString(StandardCharsets.UTF_8, true);
    }

    /**
     * 读取字符串
     *
     * @param charset
     * @return
     * @throws IOException
     */
    default String readString(final Charset charset) throws IOException {
        return readString(charset, true);
    }

    /**
     * 读取字符串
     *
     * @param charset
     * @param shortLength
     * @return
     * @throws IOException
     */
    default String readString(final Charset charset, final boolean shortLength) throws IOException {
        int len = shortLength ? readShort() : readInt();
        if (len < 0) {
            return null;
        } else if (len == 0) {
            return EMPTY_STRING;
        } else {
            byte[] bytes = new byte[len];
            read(bytes);
            return new String(bytes, charset);
        }
    }

    @Override
    default int skipBytes(final int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total < n) && ((cur = (int) skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

    @Override
    default long skip(long n) throws IOException {
        long remaining = n;
        int nr;

        if (n <= 0) {
            return 0;
        }

        int size = (int) Math.min(2048, remaining);
        byte[] skipBuffer = new byte[size];
        while (remaining > 0) {
            nr = read(skipBuffer, 0, (int) Math.min(size, remaining));
            if (nr < 0) {
                break;
            }
            remaining -= nr;
        }

        return n - remaining;
    }
}
