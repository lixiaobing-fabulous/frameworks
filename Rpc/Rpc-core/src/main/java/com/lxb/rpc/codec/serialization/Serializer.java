package com.lxb.rpc.codec.serialization;



import com.lxb.rpc.exception.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * 对象序列化
 */
public interface Serializer {


    /**
     * 序列化
     *
     * @param os
     * @param object
     * @param <T>
     * @throws SerializerException
     */
    <T> void serialize(OutputStream os, T object) throws SerializerException;

    /**
     * 反序列化
     *
     * @param is
     * @param type
     * @param <T>
     * @return
     * @throws SerializerException
     */
    <T> T deserialize(InputStream is, Type type) throws SerializerException;
}
