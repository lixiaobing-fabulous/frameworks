package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ASMDeserializerFactory;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.parser.deserializer.ThrowableDeserializer;

import java.lang.reflect.Type;

public class JsonConfig extends ParserConfig {


    public JsonConfig() {
    }

    public JsonConfig(boolean fieldBase) {
        super(fieldBase);
    }

    public JsonConfig(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    public JsonConfig(ASMDeserializerFactory asmFactory) {
        super(asmFactory);
    }

    public ObjectDeserializer getDeserializer(final Class<?> clazz, final Type type) {
        ObjectDeserializer deserializer = super.getDeserializer(clazz, type);
        if (deserializer instanceof ThrowableDeserializer) {
            //覆盖掉默认的异常解析器
            deserializer = new JsonThrowableDeserializer(this, clazz);
            //内部支持并发
            putDeserializer(type, deserializer);
        }
        return deserializer;
    }
}
