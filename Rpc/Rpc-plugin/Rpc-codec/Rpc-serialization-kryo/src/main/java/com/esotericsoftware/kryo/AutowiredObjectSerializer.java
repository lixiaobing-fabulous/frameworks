package com.esotericsoftware.kryo;


/**
 * 自动注册自定义序列化
 *
 * @param <T>
 */
public abstract class AutowiredObjectSerializer<T> extends Serializer<T> {

    /**
     * 获取类型
     *
     * @return 类型
     */
    public abstract Class<T> getType();
}
