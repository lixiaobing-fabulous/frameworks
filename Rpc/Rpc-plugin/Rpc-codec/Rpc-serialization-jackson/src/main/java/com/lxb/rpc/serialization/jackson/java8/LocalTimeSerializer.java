package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;

/**
 * LocalTime序列化
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    public static final JsonSerializer INSTANCE = new LocalTimeSerializer();

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
