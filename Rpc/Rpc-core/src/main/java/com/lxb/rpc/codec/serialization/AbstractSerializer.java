package com.lxb.rpc.codec.serialization;

import com.lxb.extension.spi.SpiLoader;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * 抽象的数据序列化
 */
public abstract class AbstractSerializer implements Serializer {
    /**
     * 构造数据写对象
     *
     * @param os
     * @return
     */
    protected abstract ObjectWriter createWriter(OutputStream os, Object object) throws IOException;

    /**
     * 构造数据读对象
     *
     * @param is
     * @param clazz
     * @return
     */
    protected abstract ObjectReader createReader(InputStream is, Class clazz) throws IOException;

    @Override
    public <T> void serialize(final OutputStream os, final T object) throws SerializerException {
        ObjectWriter output = null;
        try {
            output = createWriter(os, object);
            if (object instanceof Codec) {
                ((Codec) object).encode(output);
            } else {
                output.writeObject(object);
            }
            output.flush();
        } catch (IOException e) {
            throw new SerializerException("Error occurred while serializing class " + object.getClass().getName(), e);
        } finally {
            if (output != null) {
                output.release();
            }
        }
    }

    @Override
    public <T> T deserialize(final InputStream is, Type type) throws SerializerException {
        if (!(type instanceof Class)) {
            throw new SerializerException("type must be a Class " + type);
        }
        try {
            Class<T>     clazz = (Class<T>) type;
            ObjectReader input = createReader(is, clazz);
            if (Codec.class.isAssignableFrom(clazz)) {
                Codec codec = (Codec) clazz.newInstance();
                codec.decode(input);
                return (T) codec;
            }
            return input.readObject(clazz);
        } catch (Exception e) {
            throw new SerializerException("Error occurred while deserializing class " + type, e);
        }
    }

    /**
     * 注册插件
     *
     * @param clazz    类型
     * @param consumer 消费者
     * @param <T>
     */
    protected static <T> void register(Class<T> clazz, Consumer<T> consumer) {
        //注册插件，便于第三方协议注册序列化实现，插件都要求单例
        SpiLoader.INSTANCE.load(clazz).forEach(plugin -> consumer.accept(plugin.getTarget()));
    }
}