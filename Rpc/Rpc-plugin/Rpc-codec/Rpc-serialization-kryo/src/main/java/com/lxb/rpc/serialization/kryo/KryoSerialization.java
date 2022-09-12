package com.lxb.rpc.serialization.kryo;


import com.esotericsoftware.kryo.AutowiredObjectSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.serialization.AbstractSerializer;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.BitSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.RegexSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.lxb.rpc.util.ClassUtils.getDefaultConstructor;
import static com.lxb.rpc.util.ClassUtils.isJavaClass;

/**
 * kryo
 */
@Extension(value = "kryo", provider = "esotericsoftware", order = Serialization.ORDER_KRYO)
@ConditionalOnClass({"com.esotericsoftware.kryo.Kryo", "de.javakaffee.kryoserializers.JdkProxySerializer"})
public class KryoSerialization implements Serialization {

    @Override
    public byte getTypeId() {
        return KRYO_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-kryo";
    }

    @Override
    public Serializer getSerializer() {
        return KryoSerializer.INSTANCE;
    }

    /**
     * Kryo序列化和反序列化实现
     */
    protected static final class KryoSerializer extends AbstractSerializer {


        /**
         * 绑定在线程变量里面
         */
        protected static final ThreadLocal<Kryo> local = ThreadLocal.withInitial(() -> {
            final Kryo kryo = new CompatibleKryo();
            kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());
            kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
            kryo.register(InvocationHandler.class, new JdkProxySerializer());
            kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
            kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
            kryo.register(Pattern.class, new RegexSerializer());
            kryo.register(BitSet.class, new BitSetSerializer());
            kryo.register(URI.class, new URISerializer());
            kryo.register(UUID.class, new UUIDSerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            SynchronizedCollectionsSerializer.registerSerializers(kryo);

            // now just added some very common classes
            kryo.register(HashMap.class);
            kryo.register(ArrayList.class);
            kryo.register(LinkedList.class);
            kryo.register(HashSet.class);
            kryo.register(TreeSet.class);
            kryo.register(Hashtable.class);
            kryo.register(Date.class);
            kryo.register(Calendar.class);
            kryo.register(ConcurrentHashMap.class);
            kryo.register(SimpleDateFormat.class);
            kryo.register(GregorianCalendar.class);
            kryo.register(Vector.class);
            kryo.register(BitSet.class);
            kryo.register(StringBuffer.class);
            kryo.register(StringBuilder.class);
            kryo.register(Object.class);
            kryo.register(Object[].class);
            kryo.register(String[].class);
            kryo.register(byte[].class);
            kryo.register(char[].class);
            kryo.register(int[].class);
            kryo.register(float[].class);
            kryo.register(double[].class);
            //注册插件，便于第三方协议注册序列化实现
            register(AutowiredObjectSerializer.class, o -> kryo.addDefaultSerializer(o.getType(), o));
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            return kryo;
        });

        protected static final KryoSerializer INSTANCE = new KryoSerializer();

        protected KryoSerializer() {
        }

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) throws IOException {
            return new KryoWriter(local.get(), new Output(os));
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) throws IOException {
            return new KryoReader(local.get(), new Input(is));
        }

    }

    /**
     * 兼容Kryo
     */
    protected static class CompatibleKryo extends Kryo {


        @Override
        public com.esotericsoftware.kryo.Serializer getDefaultSerializer(Class type) {
            if (type == null) {
                throw new KryoException("type cannot be null.");
            }

            /**
             * Kryo requires every class to provide a zero argument constructor. For any class does not match this condition, kryo have two ways:
             * 1. Use JavaSerializer,
             * 2. Set 'kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));', StdInstantiatorStrategy can generate an instance bypassing the constructor.
             *
             * In practice, it's not possible for users to register kryo Serializer for every customized class. So in most cases, customized classes with/without zero argument constructor will
             * default to the default serializer.
             * It is the responsibility of kryo to handle with every standard jdk classes, so we will just escape these classes.
             */
            if (!isJavaClass(type) && !type.isArray() && !type.isEnum() && getDefaultConstructor(type) == null) {
                return new JavaSerializer();
            }
            return super.getDefaultSerializer(type);
        }

        @Override
        public com.esotericsoftware.kryo.Registration readClass(Input input) {
            return super.readClass(input);
        }
    }
}
