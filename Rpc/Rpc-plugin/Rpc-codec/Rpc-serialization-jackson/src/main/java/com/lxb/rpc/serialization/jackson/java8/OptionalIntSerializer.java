package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.OptionalInt;

/**
 * OptionalInt序列化
 */
public class OptionalIntSerializer extends JsonSerializer<OptionalInt> {

    public static final JsonSerializer INSTANCE = new OptionalIntSerializer();

    @Override
    public void serialize(final OptionalInt value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (!value.isPresent()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.getAsInt());
        }
    }
}
