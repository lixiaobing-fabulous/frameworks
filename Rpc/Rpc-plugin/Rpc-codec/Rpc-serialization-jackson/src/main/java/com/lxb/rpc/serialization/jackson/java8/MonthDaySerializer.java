package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.MonthDay;

/**
 * MonthDay序列化
 */
public class MonthDaySerializer extends JsonSerializer<MonthDay> {

    public static final JsonSerializer INSTANCE = new MonthDaySerializer();

    @Override
    public void serialize(final MonthDay value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
