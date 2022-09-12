package com.lxb.rpc.codec.Checksum;


import com.lxb.extension.Extension;
import com.lxb.rpc.codec.CodecType;

import java.nio.ByteBuffer;

/**
 * 校验和
 */
@Extension("checksum")
public interface Checksum extends CodecType {

    byte NONE = 0;

    byte CRC32 = 1;

    byte CRC32C = 2;

    /**
     * JAVA实现的CRC32的顺序值
     */
    int JAVA_CRC32_ORDER = 100;

    /**
     * 计算
     *
     * @param data
     * @return
     */
    default long compute(final byte[] data) {
        return compute(data, 0, data.length);
    }

    /**
     * 计算
     *
     * @param data
     * @param offset
     * @param length
     * @return
     */
    long compute(byte[] data, int offset, int length);

    /**
     * 计算
     *
     * @param buffer
     * @return
     */
    default long compute(final ByteBuffer buffer) {
        int pos = buffer.position();
        int limit = buffer.limit();
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return 0;
        } else if (buffer.hasArray()) {
            return compute(buffer.array(), pos + buffer.arrayOffset(), remaining);
        } else {
            byte[] b = new byte[remaining];
            buffer.get(b);
            buffer.position(limit);
            return compute(b, 0, b.length);
        }
    }
}
