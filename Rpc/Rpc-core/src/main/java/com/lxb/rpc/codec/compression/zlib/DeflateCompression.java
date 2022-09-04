package com.lxb.rpc.codec.compression.zlib;


import com.lxb.extension.Extension;
import com.lxb.rpc.codec.compression.Compression;

/**
 * Deflate压缩
 */
@Extension(value = "deflate", provider = "java", order = Compression.DEFLATE_ORDER)
public class DeflateCompression extends ZlibCompression {

    @Override
    public byte getTypeId() {
        return Compression.DEFLATE;
    }

    @Override
    public String getTypeName() {
        return "deflate";
    }
}
