package com.lxb.rpc.serialization.jackson.java8;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.time.YearMonth;

/**
 * MonthDay反序列化
 */
public class YearMonthDeserializer extends JsonDeserializer<YearMonth> {

    public static final JsonDeserializer INSTANCE = new YearMonthDeserializer();

    @Override
    public YearMonth deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return YearMonth.parse(parser.getText());
            default:
                throw new SerializerException("Error occurs while parsing YearMonth");
        }
    }
}
