package com.lxb.rpc.serialization.protostuff;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import io.protostuff.ProtobufOutput;
import io.protostuff.ProtobufReader;
import io.protostuff.ProtobufWriter;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Protostuff
 */
@Extension(value = "protobuf", provider = "protostuff", order = Serialization.ORDER_PROTOBUF)
@ConditionalOnClass("io.protostuff.runtime.RuntimeSchema")
public class ProtobufSerialization implements Serialization {

    @Override
    public byte getTypeId() {
        return PROTOBUF_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-protobuf";
    }

    @Override
    public Serializer getSerializer() {
        return ProtobufSerializer.INSTANCE;
    }

    /**
     * Protostuff序列化和反序列化实现
     */
    protected static final class ProtobufSerializer extends ProtostuffSerialization.ProtostuffSerializer {

        protected static final ProtobufSerializer INSTANCE = new ProtobufSerializer();

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) throws IOException {
            return new ProtobufWriter(RuntimeSchema.getSchema(object.getClass(), STRATEGY), new ProtobufOutput(local.get()), os);
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) throws IOException {
            return new ProtobufReader(RuntimeSchema.getSchema(clazz, STRATEGY), is, local.get());
        }
    }

}
