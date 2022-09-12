package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.YearMonth;

/**
 * YearMonth序列化
 */
public class YearMonthSerializer extends JsonSerializer<YearMonth> {

    public static final JsonSerializer INSTANCE = new YearMonthSerializer();

    @Override
    public void serialize(final YearMonth value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
