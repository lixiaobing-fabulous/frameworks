package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class ZoneOffsetSchema extends AbstractJava8Schema<ZoneOffset> {

    public static final ZoneOffsetSchema INSTANCE = new ZoneOffsetSchema();
    public static final String TOTAL_SECONDS = "totalSeconds";

    protected static final Map<String, Integer> FIELD_MAP = new HashMap(1);

    protected static Field FIELD_TOTAL_SECONDS = getWriteableField(ZoneOffset.class, TOTAL_SECONDS);

    static {
        FIELD_MAP.put(TOTAL_SECONDS, 1);
    }

    public ZoneOffsetSchema() {
        super(ZoneOffset.class);
    }

    @Override
    public String getFieldName(int number) {
        switch (number) {
            case 1:
                return TOTAL_SECONDS;
            default:
                return null;
        }
    }

    @Override
    public int getFieldNumber(final String name) {
        return FIELD_MAP.get(name);
    }

    @Override
    public ZoneOffset newMessage() {
        //不能使用0，0会缓存结果对象
        return ZoneOffset.ofTotalSeconds(1);
    }

    @Override
    public void mergeFrom(final Input input, final ZoneOffset message) throws IOException {
        while (true) {
            int number = input.readFieldNumber(this);
            switch (number) {
                case 0:
                    return;
                case 1:
                    setValue(FIELD_TOTAL_SECONDS, message, input.readInt32());
                    break;
                default:
                    input.handleUnknownField(number, this);
            }
        }
    }

    @Override
    public void writeTo(final Output output, final ZoneOffset message) throws IOException {
        output.writeInt32(1, message.getTotalSeconds(), false);
    }
}
