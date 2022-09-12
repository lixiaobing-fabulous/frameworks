package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lxb.rpc.protocol.message.ResponsePayload;

import java.io.IOException;
import java.lang.reflect.Type;

import static com.lxb.rpc.protocol.message.ResponsePayload.*;
import static com.lxb.rpc.util.ClassUtils.getCanonicalName;

/**
 * ResponsePayload序列化
 */
public class ResponsePayloadSerializer extends JsonSerializer<ResponsePayload> {

    public static final JsonSerializer INSTANCE = new ResponsePayloadSerializer();

    @Override
    public void serialize(final ResponsePayload payload, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (payload == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            Throwable exception = payload.getException();
            Object response = payload.getResponse();
            if (response != null) {
                gen.writeStringField(RES_CLASS, getTypeName(response, payload.getType()));
                gen.writeObjectField(RESPONSE, response);
            } else if (exception != null) {
                gen.writeStringField(RES_CLASS, getCanonicalName(exception.getClass()));
                gen.writeObjectField(RESPONSE, exception);
            }
            gen.writeEndObject();
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
