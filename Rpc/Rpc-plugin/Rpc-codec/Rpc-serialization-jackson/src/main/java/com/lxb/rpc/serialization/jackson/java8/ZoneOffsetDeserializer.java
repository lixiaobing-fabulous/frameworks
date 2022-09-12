package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.ZoneOffset;

/**
 * ZoneOffset反序列化
 */
public class ZoneOffsetDeserializer extends JsonDeserializer<ZoneOffset> {

    public static final JsonDeserializer INSTANCE = new ZoneOffsetDeserializer();

    @Override
    public ZoneOffset deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return ZoneOffset.of(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing ZoneOffset");
        }
    }
}
