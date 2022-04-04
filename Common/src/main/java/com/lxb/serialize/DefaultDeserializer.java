package com.lxb.serialize;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import lombok.SneakyThrows;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class DefaultDeserializer implements Deserializer<Object> {
    @Override
    @SneakyThrows
    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            return objectInputStream.readObject();
        }

    }
}
