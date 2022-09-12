package com.lxb.rpc.serialization.jackson.java8;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.util.OptionalDouble;

/**
 * OptionalDouble反序列化
 */
public class OptionalDoubleDeserializer extends JsonDeserializer<OptionalDouble> {

    public static final JsonDeserializer INSTANCE = new OptionalDoubleDeserializer();

    @Override
    public OptionalDouble deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_NUMBER_INT:
                return OptionalDouble.of(parser.getLongValue());
            default:
                throw new SerializerException("Error occurs while parsing OptionalDouble");
        }
    }
}
