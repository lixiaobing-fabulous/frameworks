package com.lxb.rpc.serializer;

import java.io.IOException;

import static com.lxb.rpc.util.ServiceLoaders.loadDefault;


/**
 * 序列化/反序列化接口
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public interface Serializer {

    Serializer DEFAULT = loadDefault(Serializer.class);

    byte[] serialize(Object source) throws IOException;

    Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException;

}
