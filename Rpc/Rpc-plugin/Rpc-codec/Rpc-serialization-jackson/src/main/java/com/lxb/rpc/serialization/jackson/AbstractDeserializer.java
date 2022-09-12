package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 序列化
 */
public abstract class AbstractDeserializer<T> extends JsonDeserializer<T> {

    /**
     * 读取字符串
     *
     * @param parser   解析器
     * @param field    字段
     * @param nullable 是否可以null
     */
    protected String readString(final JsonParser parser, String field, boolean nullable) throws IOException {
        switch (parser.nextToken()) {
            case VALUE_STRING:
                return parser.getText();
            case VALUE_NULL:
                if (!nullable) {
                    throw new SerializerException("syntax error:" + field + " can not be null");
                }
                return null;
            default:
                throw new SerializerException("syntax error:" + field + " can not be null");
        }
    }

    /**
     * 读取字符串
     *
     * @param parser   解析器
     * @param field    字段
     * @param nullable 是否可以null
     * @param consumer 值消费者
     */
    protected void readString(final JsonParser parser, String field, boolean nullable, Consumer<String> consumer) throws IOException {
        consumer.accept(readString(parser, field, nullable));
    }
}
