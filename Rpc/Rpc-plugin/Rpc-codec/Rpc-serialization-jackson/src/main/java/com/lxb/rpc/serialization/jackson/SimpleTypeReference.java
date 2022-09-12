package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

/**
 * Jackon类型引用
 */
public class SimpleTypeReference extends TypeReference<Object> {

    protected final Type type;

    public SimpleTypeReference(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }
}
