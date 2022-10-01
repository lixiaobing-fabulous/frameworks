package com.lxb.rpc.codec.Checksum.none;

import com.lxb.extension.Extension;
import com.lxb.rpc.codec.Checksum.Checksum;

/**
 * 不做校验和
 */
@Extension("none")
public class NoneChecksum implements Checksum {
    @Override
    public long compute(final byte[] data, final int offset, final int length) {
        return 0;
    }

    @Override
    public byte getTypeId() {
        return NONE;
    }
}
