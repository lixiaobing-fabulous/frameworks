package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class OffsetDateTimeSchema extends AbstractJava8Schema<OffsetDateTime> {

    public static final OffsetDateTimeSchema INSTANCE = new OffsetDateTimeSchema();
    public static final String DATE_TIME = "dateTime";
    public static final String OFFSET = "offset";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(2);

    protected static Field FIELD_DATE_TIME = getWriteableField(OffsetDateTime.class, DATE_TIME);
    protected static Field FIELD_ZONE_OFFSET = getWriteableField(OffsetDateTime.class, OFFSET);

    static {
        FIELD_MAP.put(DATE_TIME, 1);
        FIELD_MAP.put(OFFSET, 2);
    }

    public OffsetDateTimeSchema() {
        super(OffsetDateTime.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return DATE_TIME;
            case 2:
                return OFFSET;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public OffsetDateTime newMessage() {
        return OffsetDateTime.now();
    }

    @Override
    public void mergeFrom(final Input input, final OffsetDateTime message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    LocalDateTime localDateTime = LocalDateTime.now();
                    input.mergeObject(localDateTime, LocalDateTimeSchema.INSTANCE);
                    setValue(FIELD_DATE_TIME, message, localDateTime);
                    break;
                case 2:
                    //不能使用0，0会缓存结果对象
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(1);
                    input.mergeObject(offset, ZoneOffsetSchema.INSTANCE);
                    setValue(FIELD_ZONE_OFFSET, message, offset);
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final OffsetDateTime message) throws IOException {
        output.writeObject(1, message.toLocalDateTime(), LocalDateTimeSchema.INSTANCE, false);
        output.writeObject(2, message.getOffset(), ZoneOffsetSchema.INSTANCE, false);
    }
}
