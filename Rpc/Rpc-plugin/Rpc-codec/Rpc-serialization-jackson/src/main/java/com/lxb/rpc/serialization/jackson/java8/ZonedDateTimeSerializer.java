package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * ZonedDateTime序列化
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    public static final JsonSerializer INSTANCE = new ZonedDateTimeSerializer();

    @Override
    public void serialize(final ZonedDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
