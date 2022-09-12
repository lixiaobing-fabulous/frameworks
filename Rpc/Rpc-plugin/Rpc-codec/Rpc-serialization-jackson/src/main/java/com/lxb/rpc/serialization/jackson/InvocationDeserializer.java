package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lxb.rpc.exception.MethodOverloadException;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.Invocation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.lxb.rpc.protocol.message.Invocation.*;

/**
 * Invocation反序列化
 */
public class InvocationDeserializer extends AbstractDeserializer<Invocation> {

    public static final JsonDeserializer INSTANCE = new InvocationDeserializer();

    @Override
    public Invocation deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        switch (parser.currentToken()) {
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return parse(parser);
            default:
                throw new SerializerException("Error occurs while parsing invocation");
        }

    }

    /**
     * 解析
     *
     * @param parser 解析器
     * @return 调用对象
     * @throws IOException
     */
    protected Invocation parse(final JsonParser parser) throws IOException {
        Invocation invocation = new Invocation();
        String key;
        try {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                key = parser.currentName();
                if (CLASS_NAME.equals(key)) {
                    readString(parser, CLASS_NAME, false, v -> invocation.setClassName(v));
                } else if (ALIAS.equals(key)) {
                    readString(parser, ALIAS, true, v -> invocation.setAlias(v));
                } else if (METHOD_NAME.equals(key)) {
                    readString(parser, METHOD_NAME, true, v -> invocation.setMethodName(v));
                } else if (ARGS_TYPE.equals(key)) {
                    invocation.setArgsType(readArgTypes(parser));
                } else if (ARGS.equals(key)) {
                    invocation.setArgs(parseArgs(parser, invocation.computeTypes()));
                } else if (ATTACHMENTS.equals(key)) {
                    invocation.addAttachments(readAttachments(parser));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | MethodOverloadException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return invocation;
    }

    /**
     * 读取字符串数组
     *
     * @param parser 解析器
     */
    protected String[] readArgTypes(final JsonParser parser) throws IOException {
        switch (parser.nextToken()) {
            case START_ARRAY:
                return parser.readValueAs(String[].class);
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("Error occurs while parsing invocation");
        }
    }

    /**
     * 读取扩展属性
     *
     * @param parser 解析器
     */
    protected Map<String, Object> readAttachments(final JsonParser parser) throws IOException {
        switch (parser.nextToken()) {
            case START_OBJECT:
                return parser.readValueAs(new TypeReference<Map<String, Object>>() {
                });
            case VALUE_NULL:
                return null;
            default:
                throw new SerializerException("Error occurs while parsing invocation");
        }
    }

    /**
     * 解析参数
     *
     * @param parser 解析器
     * @param types  类型
     */
    protected Object[] parseArgs(final JsonParser parser, final Type[] types) throws IOException {
        switch (parser.nextToken()) {
            case START_ARRAY:
                //解析参数
                Object[] objects = new Object[types.length];
                for (int i = 0; i < objects.length; i++) {
                    parser.nextToken();
                    objects[i] = parser.readValueAs(new SimpleTypeReference(types[i]));
                }
                if (parser.nextToken() != END_ARRAY) {
                    throw new SerializerException("The argument size must be " + types.length);
                }
                return objects;
            case VALUE_NULL:
                if (types.length == 0) {
                    return new Object[0];
                } else {
                    throw new SerializerException("syntax error: args can not be null");
                }
            default:
                throw new SerializerException("Error occurs while parsing invocation");
        }
    }
}
