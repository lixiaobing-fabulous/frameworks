package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.OptionalDouble;

/**
 * OptionalDouble序列化
 */
public class OptionalDoubleSerializer extends JsonSerializer<OptionalDouble> {

    public static final JsonSerializer INSTANCE = new OptionalDoubleSerializer();

    @Override
    public void serialize(final OptionalDouble value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (!value.isPresent()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.getAsDouble());
        }
    }
}
