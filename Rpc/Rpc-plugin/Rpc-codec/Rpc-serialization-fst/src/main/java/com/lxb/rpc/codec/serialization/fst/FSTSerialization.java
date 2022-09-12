package com.lxb.rpc.codec.serialization.fst;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.serialization.AbstractSerializer;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import org.nustaq.serialization.AutowiredObjectSerializer;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * fast-serialization
 */
@Extension(value = "fst", provider = "nustaq", order = Serialization.ORDER_FST)
@ConditionalOnClass("org.nustaq.serialization.FSTConfiguration")
public class FSTSerialization implements Serialization {

    @Override
    public byte getTypeId() {
        return FST_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-fst";
    }

    @Override
    public Serializer getSerializer() {
        return FSTSerializer.INSTANCE;
    }

    /**
     * FST序列化和反序列化实现
     */
    protected static final class FSTSerializer extends AbstractSerializer {


        /**
         * 单例，延迟加载
         */
        protected static final FSTSerializer INSTANCE = new FSTSerializer();
        /**
         * FST配置
         */
        protected static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

        static {
            //注册插件，便于第三方协议注册序列化实现
            register(AutowiredObjectSerializer.class, o -> fst.registerSerializer(o.getType(), o, false));
        }

        protected FSTSerializer() {
        }

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) throws IOException {
            return new FSTObjectWriter(fst.getObjectOutput(os));
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) throws IOException {
            return new FSTObjectReader(fst.getObjectInput(is));
        }

    }
}
