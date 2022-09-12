package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lxb.rpc.protocol.message.Invocation;

import java.io.IOException;
import java.util.Map;

import static com.lxb.rpc.protocol.message.Invocation.*;

/**
 * Invocation序列化
 */
public class InvocationSerializer extends JsonSerializer<Invocation> {

    public static final JsonSerializer INSTANCE = new InvocationSerializer();

    @Override
    public void serialize(final Invocation invocation, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (invocation == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            gen.writeStringField(CLASS_NAME, invocation.getClassName());
            //2、alias
            gen.writeStringField(ALIAS, invocation.getAlias());
            //3、method name
            gen.writeStringField(METHOD_NAME, invocation.getMethodName());
            //4.argsType
            //TODO 应该根据泛型变量来决定是否要参数类型
            if (invocation.isCallback()) {
                //回调需要写上实际的参数类型
                gen.writeFieldName(ARGS_TYPE);
                String[] argsType = invocation.computeArgsType();
                gen.writeArray(argsType, 0, argsType.length);
            }
            //5、args
            gen.writeFieldName(ARGS);
            Object[] args = invocation.getArgs();
            if (args == null) {
                gen.writeNull();
            } else {
                gen.writeStartArray();
                for (Object arg : args) {
                    gen.writeObject(arg);
                }
                gen.writeEndArray();
            }
            //7、attachments
            Map<String, Object> attachments = invocation.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                gen.writeFieldName(ATTACHMENTS);
                gen.writeObject(attachments);
            }
            gen.writeEndObject();
        }
    }
}
