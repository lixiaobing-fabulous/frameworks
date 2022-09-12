package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.lxb.extension.Extension;
import com.lxb.extension.Option;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.BackupShard;
import com.lxb.rpc.codec.UnsafeByteArrayOutputStream;
import com.lxb.rpc.codec.serialization.Json;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import com.lxb.rpc.codec.serialization.TypeReference;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.Invocation;
import com.lxb.rpc.protocol.message.ResponsePayload;
import com.lxb.rpc.serialization.jackson.java8.DurationDeserializer;
import com.lxb.rpc.serialization.jackson.java8.DurationSerializer;
import com.lxb.rpc.serialization.jackson.java8.InstantDeserializer;
import com.lxb.rpc.serialization.jackson.java8.InstantSerializer;
import com.lxb.rpc.serialization.jackson.java8.LocalDateDeserializer;
import com.lxb.rpc.serialization.jackson.java8.LocalDateSerializer;
import com.lxb.rpc.serialization.jackson.java8.LocalDateTimeDeserializer;
import com.lxb.rpc.serialization.jackson.java8.LocalDateTimeSerializer;
import com.lxb.rpc.serialization.jackson.java8.LocalTimeDeserializer;
import com.lxb.rpc.serialization.jackson.java8.LocalTimeSerializer;
import com.lxb.rpc.serialization.jackson.java8.MonthDayDeserializer;
import com.lxb.rpc.serialization.jackson.java8.MonthDaySerializer;
import com.lxb.rpc.serialization.jackson.java8.OffsetDateTimeDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OffsetDateTimeSerializer;
import com.lxb.rpc.serialization.jackson.java8.OffsetTimeDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OffsetTimeSerializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalDoubleDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalDoubleSerializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalIntDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalIntSerializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalLongDeserializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalLongSerializer;
import com.lxb.rpc.serialization.jackson.java8.OptionalSerializer;
import com.lxb.rpc.serialization.jackson.java8.PeriodDeserializer;
import com.lxb.rpc.serialization.jackson.java8.PeriodSerializer;
import com.lxb.rpc.serialization.jackson.java8.YearDeserializer;
import com.lxb.rpc.serialization.jackson.java8.YearMonthDeserializer;
import com.lxb.rpc.serialization.jackson.java8.YearMonthSerializer;
import com.lxb.rpc.serialization.jackson.java8.YearSerializer;
import com.lxb.rpc.serialization.jackson.java8.ZoneIdDeserializer;
import com.lxb.rpc.serialization.jackson.java8.ZoneIdSerializer;
import com.lxb.rpc.serialization.jackson.java8.ZoneOffsetDeserializer;
import com.lxb.rpc.serialization.jackson.java8.ZoneOffsetSerializer;
import com.lxb.rpc.serialization.jackson.java8.ZonedDateTimeDeserializer;
import com.lxb.rpc.serialization.jackson.java8.ZonedDateTimeSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Function;

@Extension(value = "json", provider = "jackson", order = Serialization.ORDER_JACKSON)
@ConditionalOnClass("com.fasterxml.jackson.core.JsonFactory")
public class JacksonSerialization implements Serialization, Json {

    @Override
    public byte getTypeId() {
        return JSON_ID;
    }

    @Override
    public String getContentType() {
        return "text/json";
    }

    @Override
    public boolean autoType() {
        //在序列化Invocation的调用参数时候不支持类型，需要类名
        return false;
    }

    @Override
    public Serializer getSerializer() {
        return JacksonSerializer.INSTANCE;
    }

    @Override
    public void writeJSONString(final OutputStream os, final Object object) throws SerializerException {
        JacksonSerializer.INSTANCE.writeJSONString(os, object);
    }

    @Override
    public String toJSONString(final Object object) throws SerializerException {
        return JacksonSerializer.INSTANCE.toJSONString(object);
    }

    @Override
    public byte[] toJSONBytes(final Object object) throws SerializerException {
        return JacksonSerializer.INSTANCE.toJSONBytes(object);
    }

    @Override
    public <T> T parseObject(final String text, final Type type) throws SerializerException {
        return JacksonSerializer.INSTANCE.parseObject(text, type);
    }

    @Override
    public <T> T parseObject(final String text, final TypeReference<T> reference) throws SerializerException {
        return JacksonSerializer.INSTANCE.parseObject(text, reference);
    }

    @Override
    public <T> T parseObject(final InputStream is, final Type type) throws SerializerException {
        return JacksonSerializer.INSTANCE.parseObject(is, type);
    }

    @Override
    public <T> T parseObject(final InputStream is, final TypeReference<T> reference) throws SerializerException {
        return JacksonSerializer.INSTANCE.parseObject(is, reference);
    }

    @Override
    public void parseArray(final Reader reader, final Function<Function<Type, Object>, Boolean> function) throws SerializerException {
        JacksonSerializer.INSTANCE.parseArray(reader, function);
    }

    @Override
    public void parseObject(final Reader reader, final BiFunction<String, Function<Type, Object>, Boolean> function) throws SerializerException {
        JacksonSerializer.INSTANCE.parseObject(reader, function);
    }


    /**
     * JSON序列化和反序列化实现
     */
    protected static class JacksonSerializer implements Serializer, Json {


        protected static final JacksonSerializer INSTANCE = new JacksonSerializer();

        protected ObjectMapper mapper = new ObjectMapper();

        public JacksonSerializer() {
            ZoneId zoneId = null;
            try {
                zoneId = ZoneId.of("UTC");
                //ZoneRegion对象
            } catch (Throwable e) {
            }
            SimpleModule module = new SimpleModule();
            module.setSerializers(new MySimpleSerializers());
            module.setDeserializers(new MySimpleDeserializers());
            //TODO 增加java8的序列化
            module.addSerializer(Invocation.class, InvocationSerializer.INSTANCE);
            module.addSerializer(ResponsePayload.class, ResponsePayloadSerializer.INSTANCE);
            module.addSerializer(BackupShard.class, BackupShardSerializer.INSTANCE);
            module.addSerializer(Duration.class, DurationSerializer.INSTANCE);
            module.addSerializer(Instant.class, InstantSerializer.INSTANCE);
            module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
            module.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
            module.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
            module.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
            module.addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
            module.addSerializer(Option.class, OptionalSerializer.INSTANCE);
            module.addSerializer(OptionalDouble.class, OptionalDoubleSerializer.INSTANCE);
            module.addSerializer(OptionalInt.class, OptionalIntSerializer.INSTANCE);
            module.addSerializer(OptionalLong.class, OptionalLongSerializer.INSTANCE);
            module.addSerializer(Period.class, PeriodSerializer.INSTANCE);
            module.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
            module.addSerializer(ZoneOffset.class, ZoneOffsetSerializer.INSTANCE);
            module.addSerializer(ZoneId.class, ZoneIdSerializer.INSTANCE);
            module.addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
            module.addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);
            module.addSerializer(Year.class, YearSerializer.INSTANCE);
            module.addDeserializer(Invocation.class, InvocationDeserializer.INSTANCE);
            module.addDeserializer(ResponsePayload.class, ResponsePayloadDeserializer.INSTANCE);
            module.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
            module.addDeserializer(Instant.class, InstantDeserializer.INSTANCE);
            module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
            module.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
            module.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
            module.addDeserializer(OffsetDateTime.class, OffsetDateTimeDeserializer.INSTANCE);
            module.addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
            module.addDeserializer(Option.class, OptionalDeserializer.INSTANCE);
            module.addDeserializer(OptionalDouble.class, OptionalDoubleDeserializer.INSTANCE);
            module.addDeserializer(OptionalInt.class, OptionalIntDeserializer.INSTANCE);
            module.addDeserializer(OptionalLong.class, OptionalLongDeserializer.INSTANCE);
            module.addDeserializer(Period.class, PeriodDeserializer.INSTANCE);
            module.addDeserializer(ZonedDateTime.class, ZonedDateTimeDeserializer.INSTANCE);
            module.addDeserializer(ZoneOffset.class, ZoneOffsetDeserializer.INSTANCE);
            module.addDeserializer(ZoneId.class, ZoneIdDeserializer.INSTANCE);
            if (zoneId != null) {
                module.addDeserializer(zoneId.getClass(), ZoneIdDeserializer.INSTANCE);
            }
            module.addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
            module.addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
            module.addDeserializer(Year.class, YearDeserializer.INSTANCE);
            module.addDeserializer(Calendar.class, CalendarDeserializer.INSTANCE);
            JsonDeserializer<?> deserializer = new CalendarDeserializer(GregorianCalendar.class);
            module.addDeserializer(GregorianCalendar.class, (JsonDeserializer<GregorianCalendar>) deserializer);
            mapper.setTimeZone(TimeZone.getDefault());
            mapper.registerModule(module);
        }

        @Override
        public void writeJSONString(final OutputStream os, final Object object) throws SerializerException {
            try {
                mapper.writeValue(os, object);
            } catch (IOException e) {
                throw new SerializerException("Error occurred while serializing object", e);
            }
        }

        @Override
        public String toJSONString(final Object object) throws SerializerException {
            try {
                return mapper.writeValueAsString(object);
            } catch (IOException e) {
                throw new SerializerException("Error occurred while serializing object", e);
            }
        }

        @Override
        public byte[] toJSONBytes(final Object object) throws SerializerException {
            try {
                UnsafeByteArrayOutputStream baos = new UnsafeByteArrayOutputStream();
                mapper.writeValue(baos, object);
                return baos.toByteArray();
            } catch (IOException e) {
                throw new SerializerException("Error occurred while serializing object", e);
            }
        }

        @Override
        public <T> T parseObject(final String text, final Type type) throws SerializerException {
            if (text == null) {
                return null;
            }
            try {
                return (T) mapper.readValue(text, new SimpleTypeReference(type));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public <T> T parseObject(final String text, final TypeReference<T> reference) throws SerializerException {
            if (text == null) {
                return null;
            }
            try {
                return (T) mapper.readValue(text, new SimpleTypeReference(reference.getType()));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public <T> T parseObject(final InputStream is, Type type) throws SerializerException {
            if (is == null) {
                return null;
            }
            try {
                return (T) mapper.readValue(is, new SimpleTypeReference(type));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public <T> T parseObject(final InputStream is, final TypeReference<T> reference) throws SerializerException {
            if (is == null) {
                return null;
            }
            try {
                return (T) mapper.readValue(is, new SimpleTypeReference(reference.getType()));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public void parseArray(final Reader reader, final Function<Function<Type, Object>, Boolean> function) throws SerializerException {
            try (JsonParser parser = mapper.createParser(reader)) {
                // loop until token equal to "}"
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    if (!function.apply(o -> parseObject(parser, o))) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public void parseObject(final Reader reader, final BiFunction<String, Function<Type, Object>, Boolean> function) throws SerializerException {
            try (JsonParser parser = mapper.createParser(reader);) {
                // loop until token equal to "}"
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    if (!function.apply(parser.getCurrentName(), o -> parseObject(parser, o))) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        /**
         * 根据parser进行解析
         *
         * @param parser 解析器
         * @param type   类型
         * @return 对象
         */
        protected Object parseObject(final JsonParser parser, final Type type) {
            try {
                return parser.readValueAs(new SimpleTypeReference(type));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }

        @Override
        public <T> void serialize(final OutputStream os, final T object) throws SerializerException {
            try {
                mapper.writeValue(os, object);
            } catch (IOException e) {
                throw new SerializerException("Error occurred serializing object", e);
            }
        }

        @Override
        public <T> T deserialize(final InputStream is, final Type type) throws SerializerException {
            if (is == null) {
                return null;
            }
            try {
                return (T) mapper.readValue(is, new SimpleTypeReference(type));
            } catch (IOException e) {
                throw new SerializerException("Error occurs while parsing object", e);
            }
        }
    }

    /**
     * 提供类型的序列化器
     */
    protected static class MySimpleSerializers extends SimpleSerializers {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            JsonSerializer<?> result = super.findSerializer(config, type, beanDesc);
            if (result != null) {
                return result;
            } else if (type.isThrowable()) {
                return ThrowableSerializer.INSTANCE;
            }
            return null;
        }
    }

    /**
     * 提供类型的反序列化器
     */
    protected static class MySimpleDeserializers extends SimpleDeserializers {

        public MySimpleDeserializers() {
        }

        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
            JsonDeserializer<?> result = super.findBeanDeserializer(type, config, beanDesc);
            if (result != null) {
                return result;
            } else if (type.isThrowable()) {
                return ThrowableDeserializer.INSTANCE;
            }
            return null;
        }
    }

}
