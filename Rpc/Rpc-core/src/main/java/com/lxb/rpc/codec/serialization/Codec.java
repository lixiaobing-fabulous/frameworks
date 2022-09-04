package com.lxb.rpc.codec.serialization;


import java.io.IOException;

/**
 * 消息自带序列化
 */
public interface Codec {

    /**
     * 序列化
     *
     * @param output
     * @throws IOException
     */
    void encode(ObjectWriter output) throws IOException;

    /**
     * 反序列化
     *
     * @param input
     * @throws IOException
     */
    void decode(ObjectReader input) throws IOException;

}
