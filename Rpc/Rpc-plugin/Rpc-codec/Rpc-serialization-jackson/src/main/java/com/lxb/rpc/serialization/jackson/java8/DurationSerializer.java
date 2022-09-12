package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

/**
 * Duration序列化
 */
public class DurationSerializer extends JsonSerializer<Duration> {

    public static final JsonSerializer INSTANCE = new DurationSerializer();

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
