package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

public class YearSchema extends AbstractJava8Schema<Year> {

    public static final YearSchema INSTANCE = new YearSchema();
    public static final String YEAR = "year";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(1);

    protected static Field FIELD_YEAR = getWriteableField(Year.class, YEAR);

    static {
        FIELD_MAP.put(YEAR, 1);
    }


    public YearSchema() {
        super(Year.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return YEAR;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public Year newMessage() {
        return Year.of(2000);
    }

    @Override
    public void mergeFrom(final Input input, final Year message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    setValue(FIELD_YEAR, message, input.readInt32());
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final Year message) throws IOException {
        output.writeInt32(1, message.getValue(), false);
    }
}
