package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.LocalTime;

/**
 * LocalTime反序列化
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    public static final JsonDeserializer INSTANCE = new LocalTimeDeserializer();

    @Override
    public LocalTime deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return LocalTime.parse(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing LocalTime");
        }
    }
}
