package io.protostuff;


import io.protostuff.Schema;

/**
 * 自动注册序列化
 *
 * @param <T>
 */
public interface AutowiredObjectSerializer<T> extends Schema<T> {
    /**
     * 获取类型
     *
     * @return 类型
     */
    default Class<? super T> getType() {
        return typeClass();
    }
}
