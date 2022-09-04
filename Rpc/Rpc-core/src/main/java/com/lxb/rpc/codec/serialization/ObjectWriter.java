package com.lxb.rpc.codec.serialization;

import java.io.IOException;
import java.io.ObjectOutput;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface ObjectWriter extends ObjectOutput {
    @Override
    default void write(final int b) throws IOException {
        writeByte(b);
    }

    @Override
    default void write(final byte[] b) throws IOException {
        write(b, 0, b == null ? 0 : b.length);
    }

    @Override
    default void writeChars(final String s) throws IOException {
        if (s == null) {
            return;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            writeChar(s.charAt(i));
        }
    }

    @Override
    default void writeBytes(final String s) throws IOException {
        write(s == null ? null : s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    default void writeUTF(final String s) throws IOException {
        writeString(s, StandardCharsets.UTF_8, false, true);
    }

    /**
     * 写字符串
     *
     * @param v
     * @throws IOException
     */
    default void writeString(final String v) throws IOException {
        writeString(v, StandardCharsets.UTF_8, false, true);
    }

    /**
     * 写字符串
     *
     * @param value
     * @param charset
     * @return
     * @throws IOException
     */
    default void writeString(final String value, final Charset charset) throws IOException {
        writeString(value, charset, false, true);
    }

    /**
     * 写字符串
     *
     * @param value       字符串
     * @param charset     字符集
     * @param zeroNull    NULL字符串长度写0，否则写-1
     * @param shortLength 字符串长度为2个字节
     * @return
     * @throws IOException
     */
    default void writeString(final String value, final Charset charset, final boolean zeroNull, final boolean shortLength) throws IOException {
        int length = value == null ? (zeroNull ? 0 : -1) : value.length();
        if (shortLength) {
            writeShort(length);
        } else {
            writeInt(length);
        }
        if (length > 0) {
            write(value.getBytes(charset == null ? StandardCharsets.UTF_8 : charset));
        }
    }

    /**
     * 释放资源
     */
    default void release() {
    }
}
