package com.lxb.resilience.utils;

import java.util.Objects;

public class ClassUtils {
    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param superType  the super type
     * @param targetType the target type
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 1.0.0
     */
    public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
        // any argument is null
        if (superType == null || targetType == null) {
            return false;
        }
        // equals
        if (Objects.equals(superType, targetType)) {
            return true;
        }
        // isAssignableFrom
        return superType.isAssignableFrom(targetType);
    }

    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param targetType the target type
     * @param superTypes the super types
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 1.0.0
     */
    public static boolean isDerived(Class<?> targetType, Class<?>... superTypes) {
        // any argument is null
        if (superTypes == null || superTypes.length == 0 || targetType == null) {
            return false;
        }
        boolean derived = false;
        for (Class<?> superType : superTypes) {
            if (isAssignableFrom(superType, targetType)) {
                derived = true;
                break;
            }
        }
        return derived;
    }
}
