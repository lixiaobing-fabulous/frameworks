package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneOffset;

/**
 * ZoneOffset序列化
 */
public class ZoneOffsetSerializer extends JsonSerializer<ZoneOffset> {

    public static final JsonSerializer INSTANCE = new ZoneOffsetSerializer();

    @Override
    public void serialize(final ZoneOffset value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
