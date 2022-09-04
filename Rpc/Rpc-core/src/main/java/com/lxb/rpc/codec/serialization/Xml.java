package com.lxb.rpc.codec.serialization;



import com.lxb.rpc.exception.SerializerException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Xml
 */
public interface Xml {

    /**
     * 序列化
     *
     * @param target
     * @return
     * @throws Exception
     */
    default String marshall(final Object target) throws SerializerException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        marshall(os, target);
        return new String(os.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 序列化
     *
     * @param os
     * @param target
     * @throws Exception
     */
    void marshall(OutputStream os, Object target) throws SerializerException;

    /**
     * 序列化
     *
     * @param writer
     * @param target
     * @throws Exception
     */
    void marshall(Writer writer, Object target) throws SerializerException;

    /**
     * 反序列化
     *
     * @param reader
     * @param clazz
     * @return
     * @throws Exception
     */
    <T> T unmarshall(Reader reader, Class<T> clazz) throws SerializerException;

    /**
     * 反序列化
     *
     * @param is
     * @param clazz
     * @return
     * @throws Exception
     */
    <T> T unmarshall(InputStream is, Class<T> clazz) throws SerializerException;

    /**
     * 反序列化
     *
     * @param value
     * @param clazz
     * @return
     * @throws Exception
     */
    default <T> T unmarshall(final String value, final Class<T> clazz) throws SerializerException {
        return unmarshall(new StringReader(value), clazz);
    }

}
