package com.lxb.rpc.fastjson.java8;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.YearMonth;

public class YearMonthSerialization implements ObjectSerializer, ObjectDeserializer {

    public static final YearMonthSerialization INSTANCE = new YearMonthSerialization();

    @Override
    public <T> T deserialze(final DefaultJSONParser parser, final Type type, final Object fieldName) {
        String text = parser.parseObject(String.class);
        return (T) YearMonth.parse(text);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

    @Override
    public void write(final JSONSerializer serializer, final Object object,
                      final Object fieldName, final Type fieldType, final int features) throws IOException {
        serializer.write(object.toString());
    }
}
