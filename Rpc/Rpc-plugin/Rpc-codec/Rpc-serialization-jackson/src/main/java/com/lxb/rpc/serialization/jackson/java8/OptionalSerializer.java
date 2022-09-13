package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

/**
 * Optional序列化
 */
public class OptionalSerializer extends JsonSerializer<Optional> {

    public static final JsonSerializer INSTANCE = new OptionalSerializer();

    @Override
    public void serialize(final Optional value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (!value.isPresent()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.get());
        }
    }
}
