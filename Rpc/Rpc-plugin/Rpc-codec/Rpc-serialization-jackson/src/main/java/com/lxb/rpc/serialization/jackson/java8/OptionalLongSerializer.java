package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.OptionalLong;

/**
 * OptionalLong序列化
 */
public class OptionalLongSerializer extends JsonSerializer<OptionalLong> {

    public static final JsonSerializer INSTANCE = new OptionalLongSerializer();

    @Override
    public void serialize(final OptionalLong value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (!value.isPresent()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.getAsLong());
        }
    }
}
