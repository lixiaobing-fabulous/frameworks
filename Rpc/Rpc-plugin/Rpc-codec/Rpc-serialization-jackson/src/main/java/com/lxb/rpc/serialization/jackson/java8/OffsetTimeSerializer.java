package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetTime;

/**
 * OffsetTime序列化
 */
public class OffsetTimeSerializer extends JsonSerializer<OffsetTime> {

    public static final JsonSerializer INSTANCE = new OffsetTimeSerializer();

    @Override
    public void serialize(final OffsetTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
