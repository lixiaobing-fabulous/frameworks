package com.lxb.resilience.utils;

import static com.lxb.resilience.function.ThrowableSupplier.execute;

import java.lang.reflect.Constructor;
import java.util.Arrays;


/**
 * {@link Exception} Utilities class
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public class ExceptionUtils {

    public static <T extends Throwable> T wrapThrowable(Throwable source, Class<T> exceptionType) {
        String message = source.getMessage();
        Throwable cause = source.getCause();

        Constructor[] constructors = exceptionType.getConstructors();

        if (constructors.length == 0) {
            throw new IllegalArgumentException("The exceptionType must have one public constructor.");
        }

        Arrays.sort(constructors, (o1, o2) -> Integer.compare(o2.getParameterCount(), o1.getParameterCount()));

        // find the longest arguments constructor
        Constructor constructor = constructors[0];
        Class[] parameterTypes = constructor.getParameterTypes();
        int parameterTypesLength = parameterTypes.length;
        Object[] parameters = new Object[parameterTypesLength];
        for (int i = 0; i < parameterTypesLength; i++) {
            Class parameterType = parameterTypes[i];
            if (String.class.isAssignableFrom(parameterType)) {
                parameters[i] = message;
            }
            if (Throwable.class.isAssignableFrom(parameterType)) {
                parameters[i] = cause;
            }
        }
        return execute(() -> (T) constructor.newInstance(parameters));
    }
}
