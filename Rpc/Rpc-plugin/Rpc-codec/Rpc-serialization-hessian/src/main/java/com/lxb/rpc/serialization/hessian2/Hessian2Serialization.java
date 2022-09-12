package com.lxb.rpc.serialization.hessian2;


import com.lxb.extension.Extension;
import com.lxb.rpc.codec.serialization.AbstractSerializer;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import hessian.io.AutowiredObjectDeserializer;
import hessian.io.AutowiredObjectSerializer;
import hessian.io.Hessian2Output;
import hessian.io.SerializerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * hessian2序列化协议
 */
@Extension(value = "hessian", provider = "caucho", order = Serialization.ORDER_HESSIAN)
public class Hessian2Serialization implements Serialization {

    @Override
    public byte getTypeId() {
        return HESSIAN_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-hessian";
    }

    @Override
    public Serializer getSerializer() {
        return Hessian2Serializer.INSTANCE;
    }

    /**
     * Hessian2序列化和反序列化实现
     */
    protected static final class Hessian2Serializer extends AbstractSerializer {

        protected static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory(Thread.currentThread().getContextClassLoader());

        protected static final Hessian2Serializer          INSTANCE       = new Hessian2Serializer();
        /**
         * 线程缓存，优化性能
         */
        protected static final ThreadLocal<Hessian2Output> HESSIAN_OUTPUT = ThreadLocal.withInitial(() -> {
            Hessian2Output result = new Hessian2Output(null);
            result.setSerializerFactory(SERIALIZER_FACTORY);
            result.setCloseStreamOnClose(true);
            return result;
        });

        /**
         * 线程缓存，优化性能
         */
        protected static final ThreadLocal<Hessian2BWLInput> HESSIAN_INPUT = ThreadLocal.withInitial(() -> {
            Hessian2BWLInput result = new Hessian2BWLInput();
            result.setSerializerFactory(SERIALIZER_FACTORY);
            result.setCloseStreamOnClose(true);
            return result;
        });

        static {
            SERIALIZER_FACTORY.setAllowNonSerializable(true);
            Hessian2SerializerFactory factory = new Hessian2SerializerFactory();
            register(AutowiredObjectSerializer.class, o -> factory.serializers.put(o.getType(), o));
            register(AutowiredObjectDeserializer.class, o -> factory.deserializers.put(o.getType(), o));
            if (factory.deserializers.isEmpty()) {
                factory.deserializers = null;
            }
            SERIALIZER_FACTORY.addFactory(factory);
        }

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) {
            Hessian2Output output = HESSIAN_OUTPUT.get();
            output.init(os);
            return new Hessian2Writer(output);
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) {
            Hessian2BWLInput input = HESSIAN_INPUT.get();
            input.init(is);
            return new Hessian2Reader(input);
        }
    }
}
