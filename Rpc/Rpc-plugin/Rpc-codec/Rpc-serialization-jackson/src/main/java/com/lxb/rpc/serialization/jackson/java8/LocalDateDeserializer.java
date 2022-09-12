package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.LocalDate;

/**
 * LocalDate反序列化
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    public static final JsonDeserializer INSTANCE = new LocalDateDeserializer();

    @Override
    public LocalDate deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return LocalDate.parse(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing LocalDate");
        }
    }
}
