package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.util.OptionalInt;

/**
 * OptionalInt反序列化
 */
public class OptionalIntDeserializer extends JsonDeserializer<OptionalInt> {

    public static final JsonDeserializer INSTANCE = new OptionalIntDeserializer();

    @Override
    public OptionalInt deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_NUMBER_INT:
                return OptionalInt.of(parser.getIntValue());
            default:
                throw new SerializerException("Error occurs while parsing OptionalInt");
        }
    }
}
