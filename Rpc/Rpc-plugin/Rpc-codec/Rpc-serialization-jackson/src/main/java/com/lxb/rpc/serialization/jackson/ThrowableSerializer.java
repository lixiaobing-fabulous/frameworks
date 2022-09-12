package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lxb.rpc.constants.Constants;

import java.io.IOException;

/**
 * ResponsePayload序列化
 */
public class ThrowableSerializer extends JsonSerializer<Throwable> {

    public static final JsonSerializer INSTANCE = new ThrowableSerializer();

    @Override
    public void serialize(final Throwable throwable, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (throwable == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            gen.writeStringField(Constants.ANNOTATION_TYPE, throwable.getClass().getName());
            gen.writeStringField(Constants.FIELD_MESSAGE, throwable.getMessage());
            if (throwable.getCause() != null) {
                gen.writeObjectField(Constants.FIELD_CAUSE, throwable.getCause());
            }
            StackTraceElement[] traces = throwable.getStackTrace();
            if (traces != null) {
                gen.writeFieldName(Constants.FIELD_STACKTRACE);
                gen.writeStartArray();
                for (StackTraceElement trace : traces) {
                    gen.writeObject(trace);
                }
                gen.writeEndArray();
            }
            gen.writeEndObject();
        }
    }
}
