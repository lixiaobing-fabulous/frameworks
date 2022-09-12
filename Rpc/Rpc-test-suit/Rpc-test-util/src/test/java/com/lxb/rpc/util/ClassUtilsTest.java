package com.lxb.rpc.util;


import com.lxb.rpc.util.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

public class ClassUtilsTest {

    @Test
    public void testGetterSetter() {
        Map<String, Method> getter = ClassUtils.getGetter(User.class);
        Assertions.assertEquals(getter.size(), 2);
        Assertions.assertTrue(getter.containsKey("name"));
        Assertions.assertTrue(getter.containsKey("man"));
    }
}
