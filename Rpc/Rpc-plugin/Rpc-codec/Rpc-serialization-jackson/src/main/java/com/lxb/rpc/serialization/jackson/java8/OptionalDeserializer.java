package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.util.Optional;

/**
 * Optional反序列化
 */
public class OptionalDeserializer extends JsonDeserializer<Optional> {

    public static final JsonDeserializer INSTANCE = new OptionalDeserializer();

    @Override
    public Optional deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return Optional.of(ctx.readValue(parser, ctx.getContextualType()));
            default:
                throw new SerializerException("Error occurs while parsing Optional");
        }
    }
}
