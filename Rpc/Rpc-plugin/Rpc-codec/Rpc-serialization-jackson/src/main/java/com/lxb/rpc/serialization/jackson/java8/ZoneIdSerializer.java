package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;

/**
 * ZoneId序列化
 */
public class ZoneIdSerializer extends JsonSerializer<ZoneId> {

    public static final JsonSerializer INSTANCE = new ZoneIdSerializer();

    @Override
    public void serialize(final ZoneId value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
