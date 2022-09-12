package com.lxb.rpc.serialization.protostuff;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.serialization.AbstractSerializer;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import com.lxb.rpc.serialization.protostuff.schema.DurationSchema;
import com.lxb.rpc.serialization.protostuff.schema.InstantSchema;
import com.lxb.rpc.serialization.protostuff.schema.LocalDateSchema;
import com.lxb.rpc.serialization.protostuff.schema.LocalDateTimeSchema;
import com.lxb.rpc.serialization.protostuff.schema.LocalTimeSchema;
import com.lxb.rpc.serialization.protostuff.schema.LocaleSchema;
import com.lxb.rpc.serialization.protostuff.schema.MonthDaySchema;
import com.lxb.rpc.serialization.protostuff.schema.OffsetDateTimeSchema;
import com.lxb.rpc.serialization.protostuff.schema.OffsetTimeSchema;
import com.lxb.rpc.serialization.protostuff.schema.PeriodSchema;
import com.lxb.rpc.serialization.protostuff.schema.SqlDateSchema;
import com.lxb.rpc.serialization.protostuff.schema.SqlTimeSchema;
import com.lxb.rpc.serialization.protostuff.schema.SqlTimestampSchema;
import com.lxb.rpc.serialization.protostuff.schema.YearMonthSchema;
import com.lxb.rpc.serialization.protostuff.schema.YearSchema;
import com.lxb.rpc.serialization.protostuff.schema.ZoneIdSchema;
import com.lxb.rpc.serialization.protostuff.schema.ZoneOffsetSchema;
import com.lxb.rpc.serialization.protostuff.schema.ZonedDateTimeSchema;
import io.protostuff.AutowiredObjectSerializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffOutput;
import io.protostuff.ProtostuffReader;
import io.protostuff.ProtostuffWriter;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.IdStrategy;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
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
import java.util.Locale;

/**
 * Protostuff
 */
@Extension(value = "protostuff", provider = "protostuff", order = Serialization.ORDER_PROTOSTUFF)
@ConditionalOnClass("io.protostuff.runtime.RuntimeSchema")
public class ProtostuffSerialization implements Serialization {

    @Override
    public byte getTypeId() {
        return PROTOSTUFF_ID;
    }

    @Override
    public String getContentType() {
        return "application/x-protostuff";
    }

    @Override
    public Serializer getSerializer() {
        return ProtostuffSerializer.INSTANCE;
    }

    /**
     * Protostuff序列化和反序列化实现
     */
    protected static class ProtostuffSerializer extends AbstractSerializer {

        protected static final ProtostuffSerializer INSTANCE = new ProtostuffSerializer();

        protected static final DefaultIdStrategy STRATEGY = new DefaultIdStrategy(IdStrategy.DEFAULT_FLAGS |
                IdStrategy.ALLOW_NULL_ARRAY_ELEMENT);

        static {
            STRATEGY.registerPojo(Duration.class, DurationSchema.INSTANCE);
            STRATEGY.registerPojo(Instant.class, InstantSchema.INSTANCE);
            STRATEGY.registerPojo(LocalDate.class, LocalDateSchema.INSTANCE);
            STRATEGY.registerPojo(LocalTime.class, LocalTimeSchema.INSTANCE);
            STRATEGY.registerPojo(LocalDateTime.class, LocalDateTimeSchema.INSTANCE);
            STRATEGY.registerPojo(MonthDay.class, MonthDaySchema.INSTANCE);
            STRATEGY.registerPojo(OffsetDateTime.class, OffsetDateTimeSchema.INSTANCE);
            STRATEGY.registerPojo(OffsetTime.class, OffsetTimeSchema.INSTANCE);
            STRATEGY.registerPojo(Period.class, PeriodSchema.INSTANCE);
            STRATEGY.registerPojo(YearMonth.class, YearMonthSchema.INSTANCE);
            STRATEGY.registerPojo(Year.class, YearSchema.INSTANCE);
            STRATEGY.registerPojo(ZoneId.class, ZoneIdSchema.INSTANCE);
            STRATEGY.registerPojo(ZoneOffset.class, ZoneOffsetSchema.INSTANCE);
            STRATEGY.registerPojo(ZonedDateTime.class, ZonedDateTimeSchema.INSTANCE);
            STRATEGY.registerPojo(Date.class, SqlDateSchema.INSTANCE);
            STRATEGY.registerPojo(Time.class, SqlTimeSchema.INSTANCE);
            STRATEGY.registerPojo(Timestamp.class, SqlTimestampSchema.INSTANCE);
            STRATEGY.registerPojo(Locale.class, LocaleSchema.INSTANCE);
            //ID_STRATEGY.ARRAY_SCHEMA
            //注册插件，便于第三方协议注册序列化实现
            register(AutowiredObjectSerializer.class, o -> STRATEGY.registerPojo(o.getType(), o));
        }

        protected ThreadLocal<LinkedBuffer> local = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(1024));

        protected ProtostuffSerializer() {
        }

        @Override
        protected ObjectWriter createWriter(final OutputStream os, final Object object) throws IOException {
            return new ProtostuffWriter(RuntimeSchema.getSchema(object.getClass(), STRATEGY), new ProtostuffOutput(local.get(), os), os);
        }

        @Override
        protected ObjectReader createReader(final InputStream is, final Class clazz) throws IOException {
            return new ProtostuffReader(RuntimeSchema.getSchema(clazz, STRATEGY), is, local.get());
        }
    }

}
