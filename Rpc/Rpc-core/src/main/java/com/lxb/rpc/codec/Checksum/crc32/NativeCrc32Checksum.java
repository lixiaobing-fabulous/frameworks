package com.lxb.rpc.codec.Checksum.crc32;


import com.lxb.extension.Extension;
import com.lxb.rpc.codec.Checksum.Checksum;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import static com.lxb.rpc.codec.Checksum.Checksum.JAVA_CRC32_ORDER;

/**
 * 本地的CRC32实现
 */
@Extension(value = "crc32", provider = "java", order = JAVA_CRC32_ORDER)
public class NativeCrc32Checksum implements Checksum {

    @Override
    public byte getTypeId() {
        return CRC32;
    }

    @Override
    public long compute(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        return (int) crc32.getValue();
    }

    @Override
    public long compute(final ByteBuffer buffer) {
        CRC32 crc32 = new CRC32();
        crc32.update(buffer);
        return crc32.getValue();
    }
}
