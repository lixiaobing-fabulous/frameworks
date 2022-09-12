package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AutowiredObjectDeserializer;
import com.alibaba.fastjson.serializer.AutowiredObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.ResponseMessage;
import com.lxb.rpc.protocol.message.ResponsePayload;
import org.apache.commons.lang.ClassUtils;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static com.lxb.rpc.protocol.message.ResponsePayload.*;
import static com.lxb.rpc.util.ClassUtils.getCanonicalName;
import static com.lxb.rpc.util.GenericMethod.getReturnGenericType;


/**
 * 应答序列化
 */
public abstract class AbstractResponsePayloadCodec extends AbstractSerializer implements AutowiredObjectSerializer, AutowiredObjectDeserializer {

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }

    @Override
    public Set<Type> getAutowiredFor() {
        Set<Type> result = new HashSet<>(1);
        fillAutowiredFor(result);
        return result;
    }

    /**
     * 设置类型
     *
     * @param types 类型
     */
    protected void fillAutowiredFor(final Set<Type> types) {
        types.add(ResponsePayload.class);
    }

    @Override
    public <T> T deserialze(final DefaultJSONParser parser, final Type type, final Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        switch (lexer.token()) {
            case JSONToken.NULL:
                lexer.nextToken();
                return null;
            case JSONToken.LBRACE:
                return (T) parse(parser, lexer);
            default:
                return null;
        }
    }

    /**
     * 解析应答
     *
     * @param parser 解析器
     * @param lexer  文法
     * @return 应答
     */
    protected ResponsePayload parse(final DefaultJSONParser parser, final JSONLexer lexer) {
        ResponsePayload payload = new ResponsePayload();
        String key;
        int token;
        try {
            String typeName = null;
            for (; ; ) {
                // lexer.scanSymbol
                key = lexer.scanSymbol(parser.getSymbolTable());
                if (key == null) {
                    token = lexer.token();
                    if (token == JSONToken.RBRACE) {
                        lexer.nextToken(JSONToken.COMMA);
                        break;
                    } else if (token == JSONToken.COMMA) {
                        if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
                            continue;
                        }
                    }
                }
                lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
                if (RES_CLASS.equals(key)) {
                    typeName = parseString(lexer, RES_CLASS, false);
                } else if (RESPONSE.equals(key)) {
                    payload.setResponse(parseResponse(parser, lexer, typeName));
                } else if (EXCEPTION.equals(key)) {
                    payload.setException((Throwable) parseObject(parser, lexer, getThrowableType(typeName)));
                }
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                }
            }
            return payload;
        } catch (ClassNotFoundException e) {
            throw new SerializerException(e.getMessage());
        }
    }

    /**
     * 解析应答
     *
     * @param parser   解析器
     * @param lexer    文法
     * @param typeName 名称
     * @return 应答对象
     */
    protected Object parseResponse(final DefaultJSONParser parser, final JSONLexer lexer, final String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }
        try {
            return parseObject(parser, lexer, getType(typeName));
        } catch (ClassNotFoundException e) {
            //泛化调用情况下，类可能不存在
            //TODO 需要判断是泛化调用
            return parser.parse();
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

    @Override
    public void write(final JSONSerializer serializer, final Object object, final Object fieldName,
                      final Type fieldType, final int features) {
        if (object == null) {
            serializer.writeNull();
        } else {
            writeObjectBegin(serializer);
            ResponsePayload payload = (object instanceof ResponseMessage ? ((ResponseMessage<ResponsePayload>) object).getPayLoad() : (ResponsePayload) object);
            if (payload != null) {
                Throwable exception = payload.getException();
                Object response = payload.getResponse();
                if (response != null) {
                    write(serializer, RES_CLASS, getTypeName(response, payload.getType()), NONE);
                    write(serializer, RESPONSE, response, BEFORE);
                } else if (exception != null) {
                    write(serializer, RES_CLASS, getCanonicalName(exception.getClass()), NONE);
                    write(serializer, EXCEPTION, exception, BEFORE);
                }
            }
            writeObjectEnd(serializer);
        }
    }

    /**
     * 获取应答的类型名称
     *
     * @param response 应答
     * @param type     类型
     * @return 类型名称
     */
    protected String getTypeName(final Object response, final Type type) {
        return type == null ? getCanonicalName(response.getClass()) : type.getTypeName();
    }
}
