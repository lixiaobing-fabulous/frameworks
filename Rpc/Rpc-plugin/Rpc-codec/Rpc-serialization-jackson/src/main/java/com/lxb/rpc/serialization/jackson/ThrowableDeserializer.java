package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.exception.CreationException;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.util.ClassUtils;

import java.io.IOException;

/**
 * 异常解析
 */
public class ThrowableDeserializer extends AbstractDeserializer<Throwable> {

    public static final JsonDeserializer INSTANCE = new ThrowableDeserializer();

    @Override
    public Throwable deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return parse(parser);
            default:
                throw new SerializerException("Error occurs while parsing throwable.");
        }
    }

    /**
     * 解析
     *
     * @param parser 解析器
     * @return 调用对象
     * @throws IOException
     */
    protected Throwable parse(final JsonParser parser) throws IOException {
        String key;
        Class<?> clazz = null;
        Throwable cause = null;
        String message = null;
        StackTraceElement[] stackTrace = null;
        try {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                key = parser.currentName();
                if (Constants.ANNOTATION_TYPE.equals(key)) {
                    String className = readString(parser, Constants.ANNOTATION_TYPE, true);
                    if (className != null) {
                        clazz = ClassUtils.forName(className);
                    }
                } else if (Constants.FIELD_CAUSE.equals(key)) {
                    cause = parseCause(parser);
                } else if (Constants.FIELD_MESSAGE.equals(key)) {
                    message = readString(parser, Constants.FIELD_MESSAGE, true);
                } else if (Constants.FIELD_STACKTRACE.equals(key)) {
                    stackTrace = parseTrace(parser);
                }
            }
            return ClassUtils.createException(clazz, message, cause, stackTrace);
        } catch (CreationException | ClassNotFoundException e) {
            throw new SerializerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 解析根因
     *
     * @param parser
     * @return
     * @throws IOException
     */
    protected Throwable parseCause(final JsonParser parser) throws IOException {
        switch (parser.nextToken()) {
            case START_OBJECT:
                return parser.readValueAs(Throwable.class);
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("syntax error: cause is illegal.");

        }
    }

    /**
     * 解析堆栈
     *
     * @param parser
     * @return
     * @throws IOException
     */
    protected StackTraceElement[] parseTrace(final JsonParser parser) throws IOException {
        switch (parser.nextToken()) {
            case START_ARRAY:
                return parser.readValueAs(StackTraceElement[].class);
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("syntax error: stackTrace is illegal.");

        }
    }
}
