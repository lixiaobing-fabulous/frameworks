package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class LocalTimeSchema extends AbstractJava8Schema<LocalTime> {

    public static final LocalTimeSchema INSTANCE = new LocalTimeSchema();
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";
    public static final String NANO = "nano";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(4);

    protected static Field FIELD_HOUR = getWriteableField(LocalTime.class, HOUR);
    protected static Field FIELD_MINUTE = getWriteableField(LocalTime.class, MINUTE);
    protected static Field FIELD_SECOND = getWriteableField(LocalTime.class, SECOND);
    protected static Field FIELD_NANO = getWriteableField(LocalTime.class, NANO);

    static {
        FIELD_MAP.put(HOUR, 1);
        FIELD_MAP.put(MINUTE, 2);
        FIELD_MAP.put(SECOND, 3);
        FIELD_MAP.put(NANO, 4);
    }


    public LocalTimeSchema() {
        super(LocalTime.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return HOUR;
            case 2:
                return MINUTE;
            case 3:
                return SECOND;
            case 4:
                return NANO;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public LocalTime newMessage() {
        return LocalTime.now();
    }

    @Override
    public void mergeFrom(final Input input, final LocalTime message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    setValue(FIELD_HOUR, message, (byte) input.readInt32());
                    break;
                case 2:
                    setValue(FIELD_MINUTE, message, (byte) input.readInt32());
                    break;
                case 3:
                    setValue(FIELD_SECOND, message, (byte) input.readInt32());
                    break;
                case 4:
                    setValue(FIELD_NANO, message, input.readInt32());
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final LocalTime message) throws IOException {
        output.writeInt32(1, message.getHour(), false);
        output.writeInt32(2, message.getMinute(), false);
        output.writeInt32(3, message.getSecond(), false);
        output.writeInt32(4, message.getNano(), false);
    }
}
