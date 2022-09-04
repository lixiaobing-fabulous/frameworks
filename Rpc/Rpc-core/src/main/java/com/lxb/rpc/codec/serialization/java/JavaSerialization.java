package com.lxb.rpc.codec.serialization.java;

import com.lxb.extension.Extension;
import com.lxb.rpc.codec.serialization.AbstractSerializer;
import com.lxb.rpc.codec.serialization.ObjectInputReader;
import com.lxb.rpc.codec.serialization.ObjectOutputWriter;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@Extension(value = "java", provider = "java", order = Serialization.ORDER_JAVA)
public class JavaSerialization implements Serialization {
    @Override
    public byte getTypeId() {
        return JAVA_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-java";
    }

    @Override
    public Serializer getSerializer() {
        return JavaSerializer.INSTANCE;
    }

    protected static class JavaSerializer extends AbstractSerializer {
        protected static final JavaSerializer INSTANCE = new JavaSerializer();

        @Override
        protected ObjectWriter createWriter(OutputStream os, Object object) throws IOException {
            return new ObjectOutputWriter(new ObjectOutputStream(os));
        }

        @Override
        protected ObjectReader createReader(InputStream is, Class clazz) throws IOException {
            return new ObjectInputReader(new ObjectInputStream(is));
        }
    }
}
