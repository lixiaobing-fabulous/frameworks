package com.lxb.rpc.codec.serialization.java;



import com.lxb.extension.Extension;
import com.lxb.rpc.codec.serialization.AdvanceObjectInputReader;
import com.lxb.rpc.codec.serialization.AdvanceObjectOutputWriter;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Java序列化
 */
@Extension(value = "java", provider = "advance", order = Serialization.ORDER_ADVANCE_JAVA)
public class AdvanceJavaSerialization extends JavaSerialization {

    @Override
    public byte getTypeId() {
        return ADVANCE_JAVA_ID;
    }

    @Override
    public Serializer getSerializer() {
        return AdvanceJavaSerializer.INSTANCE;
    }


    /**
     * Java序列化和反序列化实现
     */
    protected static class AdvanceJavaSerializer extends JavaSerializer {

        protected static final AdvanceJavaSerializer INSTANCE = new AdvanceJavaSerializer();

        protected AdvanceJavaSerializer() {
        }

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) throws IOException {
            return new AdvanceObjectOutputWriter(new ObjectOutputStream(os));
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) throws IOException {
            return new AdvanceObjectInputReader(new ObjectInputStream(is));
        }

    }
}
