package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class LocalDateTimeSchema extends AbstractJava8Schema<LocalDateTime> {

    public static final LocalDateTimeSchema INSTANCE = new LocalDateTimeSchema();
    public static final String DATE = "date";
    public static final String TIME = "time";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(2);

    protected static Field FIELD_DATE = getWriteableField(LocalDateTime.class, DATE);
    protected static Field FIELD_TIME = getWriteableField(LocalDateTime.class, TIME);

    static {
        FIELD_MAP.put(DATE, 1);
        FIELD_MAP.put(TIME, 2);
    }

    public LocalDateTimeSchema() {
        super(LocalDateTime.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return DATE;
            case 2:
                return TIME;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public LocalDateTime newMessage() {
        return LocalDateTime.now();
    }

    @Override
    public void mergeFrom(final Input input, final LocalDateTime message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    LocalDate localDate = LocalDate.now();
                    input.mergeObject(localDate, LocalDateSchema.INSTANCE);
                    setValue(FIELD_DATE, message, localDate);
                    break;
                case 2:
                    LocalTime localTime = LocalTime.now();
                    input.mergeObject(localTime, LocalTimeSchema.INSTANCE);
                    setValue(FIELD_TIME, message, localTime);
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final LocalDateTime message) throws IOException {
        output.writeObject(1, message.toLocalDate(), LocalDateSchema.INSTANCE, false);
        output.writeObject(2, message.toLocalTime(), LocalTimeSchema.INSTANCE, false);
    }
}
