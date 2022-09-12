package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.Year;

/**
 * Year反序列化
 */
public class YearDeserializer extends JsonDeserializer<Year> {

    public static final JsonDeserializer INSTANCE = new YearDeserializer();

    @Override
    public Year deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return Year.parse(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing Year");
        }
    }
}
