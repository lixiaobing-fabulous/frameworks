package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.ZoneId;

/**
 * ZoneId反序列化
 */
public class ZoneIdDeserializer extends JsonDeserializer<ZoneId> {

    public static final JsonDeserializer INSTANCE = new ZoneIdDeserializer();

    @Override
    public ZoneId deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return ZoneId.of(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing ZoneId");
        }
    }
}
