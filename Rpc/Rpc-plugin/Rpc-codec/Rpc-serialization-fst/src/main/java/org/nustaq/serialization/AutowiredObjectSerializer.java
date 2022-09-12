package org.nustaq.serialization;


/**
 * 自动注册自定义序列化器
 */
public interface AutowiredObjectSerializer extends FSTObjectSerializer {

    /**
     * 字段注册的类型
     *
     * @return 类型
     */
    Class<?> getType();

}
