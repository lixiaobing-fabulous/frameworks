package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InstantSchema extends AbstractJava8Schema<Instant> {

    public static final InstantSchema INSTANCE = new InstantSchema();
    public static final String SECONDS = "seconds";
    public static final String NANOS = "nanos";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(2);

    protected static Field FIELD_SECONDS = getWriteableField(Instant.class, SECONDS);
    protected static Field FIELD_NANOS = getWriteableField(Instant.class, NANOS);

    static {
        FIELD_MAP.put(SECONDS, 1);
        FIELD_MAP.put(NANOS, 2);
    }


    public InstantSchema() {
        super(Instant.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return SECONDS;
            case 2:
                return NANOS;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public Instant newMessage() {
        return Instant.now();
    }

    @Override
    public void mergeFrom(final Input input, final Instant message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    setValue(FIELD_SECONDS, message, input.readInt64());
                    break;
                case 2:
                    setValue(FIELD_NANOS, message, input.readInt32());
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final Instant message) throws IOException {
        output.writeInt64(1, message.getEpochSecond(), false);
        output.writeInt32(2, message.getNano(), false);
    }
}
