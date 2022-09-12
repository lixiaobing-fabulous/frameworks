package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * OffsetDateTime序列化
 */
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {

    public static final JsonSerializer INSTANCE = new OffsetDateTimeSerializer();

    @Override
    public void serialize(final OffsetDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
