package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.ResponsePayload;
import com.lxb.rpc.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import static com.lxb.rpc.protocol.message.ResponsePayload.*;
import static com.lxb.rpc.util.GenericMethod.getReturnGenericType;


/**
 * ResponsePayload反序列化
 */
public class ResponsePayloadDeserializer extends AbstractDeserializer<ResponsePayload> {

    public static final JsonDeserializer INSTANCE = new ResponsePayloadDeserializer();

    @Override
    public ResponsePayload deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return parse(parser);
            default:
                throw new SerializerException("Error occurs while parsing responsePayload");
        }

    }

    /**
     * 解析
     *
     * @param parser 解析器
     * @return 应答
     * @throws IOException
     */
    protected ResponsePayload parse(final JsonParser parser) throws IOException {
        ResponsePayload payload = new ResponsePayload();
        String key;
        String typeName = null;
        try {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                key = parser.currentName();
                if (RES_CLASS.equals(key)) {
                    typeName = readString(parser, RES_CLASS, false);
                } else if (RESPONSE.equals(key)) {
                    payload.setResponse(readResponse(parser, typeName));
                } else if (EXCEPTION.equals(key)) {
                    payload.setException(readException(parser, typeName));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return payload;
    }

    /**
     * 读取应答
     *
     * @param parser   解析器
     * @param typeName 类型
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object readResponse(final JsonParser parser, final String typeName) throws IOException, ClassNotFoundException {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }
        switch (parser.nextToken()) {
            case START_OBJECT:
                return parser.readValueAs(new SimpleTypeReference(getType(typeName)));
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("Error occurs while parsing responsePayload");
        }
    }

    /**
     * 读取扩展属性
     *
     * @param parser   解析器
     * @param typeName 类型
     * @throws IOException
     */
    protected Throwable readException(final JsonParser parser, final String typeName) throws IOException, ClassNotFoundException {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }
        switch (parser.nextToken()) {
            case START_OBJECT:
                return (Throwable) parser.readValueAs(getThrowableType(typeName));
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("Error occurs while parsing responsePayload");
        }
    }

    /**
     * 获取异常类型
     *
     * @param typeName 类型名称
     * @return 异常类型
     * @throws ClassNotFoundException
     */
    protected Class<?> getThrowableType(final String typeName) throws ClassNotFoundException {
        Class<?> clazz = ClassUtils.getClass(typeName);
        if (clazz == null) {
            return Throwable.class;
        } else if (Throwable.class.isAssignableFrom(clazz)) {
            return clazz;
        } else {
            throw new SerializerException("syntax error: invalid throwable class " + typeName);
        }
    }

    /**
     * 根据类型名称获取类型
     *
     * @param typeName 类型名称
     * @return 类型
     * @throws ClassNotFoundException
     */
    protected Type getType(final String typeName) throws ClassNotFoundException {
        Type type = getReturnGenericType(typeName);
        type = type == null ? ClassUtils.getClass(typeName) : type;
        return type;
    }

}
