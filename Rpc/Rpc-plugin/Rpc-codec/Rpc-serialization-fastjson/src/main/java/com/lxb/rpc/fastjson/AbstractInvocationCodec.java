package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AutowiredObjectDeserializer;
import com.alibaba.fastjson.serializer.AutowiredObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.lxb.rpc.exception.MethodOverloadException;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.Call;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static com.lxb.rpc.protocol.message.Invocation.*;

/**
 * Title: Invocation fastjson 序列化<br>
 * <p/>
 * Description: <br>
 * 保证序列化字段按如下顺序：<br>
 * 1、class name 即接口名称<br>
 * 2、alias<br>
 * 3、method name<br>
 * 4、argsType callback 调用才会写
 * 5、args 参数value<br>
 * 6、attachments (值不为空则序列化)<br>
 * <p/>
 */
public abstract class AbstractInvocationCodec extends AbstractSerializer implements AutowiredObjectSerializer, AutowiredObjectDeserializer {

    protected String classNameKey = CLASS_NAME;
    protected String aliasKey = ALIAS;
    protected String methodNameKey = METHOD_NAME;
    protected String argsTypeKey = ARGS_TYPE;
    protected String argsKey = ARGS;
    protected String attachmentsKey = ATTACHMENTS;

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
    protected abstract void fillAutowiredFor(Set<Type> types);

    @Override
    public void write(final JSONSerializer serializer, final Object object, final Object fieldName, final Type fieldType, final int features) throws IOException {
        if (object == null) {
            serializer.writeNull();
        } else {
            Call call = (Call) object;
            writeObjectBegin(serializer);
            //1、class name
            writeString(serializer, classNameKey, call.getClassName());
            //2、alias
            writeString(serializer, aliasKey, call.getAlias());
            //3、method name
            writeString(serializer, methodNameKey, call.getMethodName());
            //4.argsType
            //TODO 应该根据泛型变量来决定是否要参数类型
            if (call.isCallback()) {
                //回调需要写上实际的参数类型
                write(serializer, argsTypeKey, call.computeArgsType(), AFTER);
            }
            //5、args
            writeArgs(serializer, call);
            //7、attachments
            write(serializer, attachmentsKey, call.getAttachments(), true, BEFORE);
            writeObjectEnd(serializer);
        }
    }

    /**
     * 写参数
     *
     * @param serializer 序列化器
     * @param call       调用对象
     */
    protected void writeArgs(final JSONSerializer serializer, final Call call) {
        write(serializer, argsKey, call.getArgs(), false, NONE);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
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
     * 解析Invocation
     *
     * @param parser 解析器
     * @param lexer  文法
     * @return
     */
    protected Call parse(final DefaultJSONParser parser, final JSONLexer lexer) {
        Call invocation = createInvocation();
        String key;
        int token;
        try {
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

                if (classNameKey.equals(key)) {
                    invocation.setClassName(parseString(lexer, classNameKey, false));
                } else if (aliasKey.equals(key)) {
                    invocation.setAlias(parseString(lexer, aliasKey, true));
                } else if (methodNameKey.equals(key)) {
                    invocation.setMethodName(parseString(lexer, methodNameKey, false));
                } else if (argsTypeKey.equals(key)) {
                    invocation.setArgsType(parseStrings(parser, lexer, argsTypeKey));
                } else if (argsKey.equals(key)) {
                    invocation.setArgs(parseObjects(parser, lexer, invocation.computeTypes(), argsKey));
                } else if (attachmentsKey.equals(key)) {
                    invocation.addAttachments(parseMap(parser, lexer, attachmentsKey));
                }
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                }
            }
            return invocation;
        } catch (ClassNotFoundException | NoSuchMethodException | MethodOverloadException e) {
            throw new SerializerException(e.getMessage());
        }

    }

    /**
     * 创建调用
     *
     * @return 调用
     */
    protected abstract Call createInvocation();


}
