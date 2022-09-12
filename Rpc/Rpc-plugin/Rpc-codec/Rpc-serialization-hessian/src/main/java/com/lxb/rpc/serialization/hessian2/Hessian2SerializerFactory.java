package com.lxb.rpc.serialization.hessian2;



import hessian.io.AbstractSerializerFactory;
import hessian.io.Deserializer;
import hessian.io.HessianProtocolException;
import hessian.io.Serializer;
import hessian.io.java8.DurationHandle;
import hessian.io.java8.InstantHandle;
import hessian.io.java8.Java8TimeSerializer;
import hessian.io.java8.LocalDateHandle;
import hessian.io.java8.LocalDateTimeHandle;
import hessian.io.java8.LocalTimeHandle;
import hessian.io.java8.MonthDayHandle;
import hessian.io.java8.OffsetDateTimeHandle;
import hessian.io.java8.OffsetTimeHandle;
import hessian.io.java8.PeriodHandle;
import hessian.io.java8.YearHandle;
import hessian.io.java8.YearMonthHandle;
import hessian.io.java8.ZoneIdHandle;
import hessian.io.java8.ZoneOffsetHandle;
import hessian.io.java8.ZonedDateTimeHandle;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义序列化器工厂类
 */
public class Hessian2SerializerFactory extends AbstractSerializerFactory {

    protected Map<Class<?>, Serializer>   serializers   = new HashMap<>();
    protected Map<Class<?>, Deserializer> deserializers = new HashMap<>();

    /**
     * 构造函数
     */
    public Hessian2SerializerFactory() {
        serializers.put(java.time.LocalTime.class, Java8TimeSerializer.of(LocalTimeHandle.class));
        serializers.put(java.time.LocalDate.class, Java8TimeSerializer.of(LocalDateHandle.class));
        serializers.put(java.time.LocalDateTime.class, Java8TimeSerializer.of(LocalDateTimeHandle.class));
        serializers.put(java.time.Instant.class, Java8TimeSerializer.of(InstantHandle.class));
        serializers.put(java.time.Duration.class, Java8TimeSerializer.of(DurationHandle.class));
        serializers.put(java.time.Period.class, Java8TimeSerializer.of(PeriodHandle.class));
        serializers.put(java.time.Year.class, Java8TimeSerializer.of(YearHandle.class));
        serializers.put(java.time.YearMonth.class, Java8TimeSerializer.of(YearMonthHandle.class));
        serializers.put(java.time.MonthDay.class, Java8TimeSerializer.of(MonthDayHandle.class));
        serializers.put(java.time.OffsetTime.class, Java8TimeSerializer.of(OffsetTimeHandle.class));
        serializers.put(java.time.ZoneOffset.class, Java8TimeSerializer.of(ZoneOffsetHandle.class));
        serializers.put(java.time.OffsetDateTime.class, Java8TimeSerializer.of(OffsetDateTimeHandle.class));
        serializers.put(java.time.ZonedDateTime.class, Java8TimeSerializer.of(ZonedDateTimeHandle.class));
        serializers.put(ZoneId.class, Java8TimeSerializer.of(ZoneIdHandle.class));
        serializers.put(ZoneId.systemDefault().getClass(), Java8TimeSerializer.of(ZoneIdHandle.class));
    }

    @Override
    public Serializer getSerializer(final Class cl) throws HessianProtocolException {
        return serializers.get(cl);
    }

    @Override
    public Deserializer getDeserializer(final Class cl) throws HessianProtocolException {
        return deserializers == null ? null : deserializers.get(cl);
    }
}
