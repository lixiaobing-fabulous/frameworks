package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.Period;

/**
 * Period反序列化
 */
public class PeriodDeserializer extends JsonDeserializer<Period> {

    public static final JsonDeserializer INSTANCE = new PeriodDeserializer();

    @Override
    public Period deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return Period.parse(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing Period");
        }
    }
}
