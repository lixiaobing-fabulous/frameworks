package com.lxb.serialize;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import lombok.SneakyThrows;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class DefaultSerializer implements Serializer<Object> {
    @Override
    @SneakyThrows
    public byte[] serialize(Object source) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(source);
            return outputStream.toByteArray();
        }
    }
}
