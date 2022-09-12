package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Year;

/**
 * Year序列化
 */
public class YearSerializer extends JsonSerializer<Year> {

    public static final JsonSerializer INSTANCE = new YearSerializer();

    @Override
    public void serialize(final Year value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
