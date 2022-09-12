package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Period;

/**
 * Period序列化
 */
public class PeriodSerializer extends JsonSerializer<Period> {

    public static final JsonSerializer INSTANCE = new PeriodSerializer();

    @Override
    public void serialize(final Period value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
