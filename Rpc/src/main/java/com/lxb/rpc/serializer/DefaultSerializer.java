package com.lxb.rpc.serializer;


import com.lxb.serialize.Deserializer;
import com.lxb.serialize.Deserializers;
import com.lxb.serialize.Serializers;

import java.io.IOException;

public class DefaultSerializer implements Serializer {

    private final Serializers serializers = new Serializers();

    private final Deserializers deserializers = new Deserializers();

    public DefaultSerializer() {
        serializers.loadSPI();
        deserializers.loadSPI();
    }

    @Override
    public byte[] serialize(Object source) throws IOException {
        Class<?> targetClass = source.getClass();
        com.lxb.serialize.Serializer serializer = serializers.getMostCompatible(targetClass);
        return serializer.serialize(source);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException {
        Deserializer deserializer = deserializers.getMostCompatible(targetClass);
        return deserializer.deserialize(bytes);
    }
}
