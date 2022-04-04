package com.lxb.utils;

import static com.lxb.function.Streams.filter;
import static com.lxb.utils.ClassUtil.getAllInterfaces;
import static com.lxb.utils.ClassUtil.getAllSuperClasses;
import static com.lxb.utils.ClassUtil.isAssignableFrom;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class TypeUtil extends BaseUtil {
    public static final Predicate<Class<?>> NON_OBJECT_TYPE_FILTER = t -> !Objects.equals(Object.class, t);

    public static <T> Class<T> findActualTypeArgumentClass(Type type, Class<?> interfaceClass, int index) {
        return (Class<T>) findActualTypeArgumentClasses(type, interfaceClass).get(index);
    }

    public static List<Class<?>> findActualTypeArgumentClasses(Type type, Class<?> interfaceClass) {
        List<Type> actualTypeArguments = findActualTypeArguments(type, interfaceClass);
        List<Class<?>> actualTypeArgumentClasses = new LinkedList<>();

        for (Type actualTypeArgument : actualTypeArguments) {
            Class<?> rawClass = getRawClass(actualTypeArgument);
            if (rawClass != null) {
                actualTypeArgumentClasses.add(rawClass);
            }
        }

        return unmodifiableList(actualTypeArgumentClasses);
    }

    public static List<Type> findActualTypeArguments(Type type, Class<?> interfaceClass) {
        List<Type> actualTypeArguments = new LinkedList<>();
        getAllGenericTypes(type, t -> isAssignableFrom(interfaceClass, getRawClass(t)))
                .forEach(parameterizedType -> {
                    Class<?> rawClass = getRawClass(parameterizedType);
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    actualTypeArguments.addAll(asList(typeArguments));
                    Class<?> superClass = rawClass.getSuperclass();
                    if (Objects.nonNull(superClass)) {
                        actualTypeArguments.addAll(findActualTypeArguments(superClass, interfaceClass));
                    }
                });
        return unmodifiableList(actualTypeArguments);
    }

    public static List<ParameterizedType> getAllGenericTypes(Type type, Predicate<ParameterizedType>... typeFilters) {
        List<ParameterizedType> all = new LinkedList<>();
        all.addAll(getAllGenericSuperClasses(type, typeFilters));
        all.addAll(getAllGenericInterfaces(type, typeFilters));
        return unmodifiableList(all);
    }

    public static List<ParameterizedType> getAllGenericInterfaces(Type type,
                                                                  Predicate<ParameterizedType>... typeFilters) {
        Class<?> rawClass = getRawClass(type);

        if (rawClass == null) {
            return emptyList();
        }

        List<Class<?>> allTypes = new LinkedList<>();
        // Add current class
        allTypes.add(rawClass);
        // Add all super classes
        allTypes.addAll(getAllSuperClasses(rawClass, NON_OBJECT_TYPE_FILTER));
        // Add all super interfaces
        allTypes.addAll(getAllInterfaces(rawClass));
        List<ParameterizedType> allGenericInterfaces = allTypes.stream()
                .map(Class::getGenericInterfaces)
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .filter(TypeUtil::isParameterizedType)
                .map(ParameterizedType.class::cast)
                .collect(Collectors.toList());
        return filter(allGenericInterfaces, typeFilters);

    }

    public static List<ParameterizedType> getAllGenericSuperClasses(Type type,
                                                                    Predicate<ParameterizedType>... typedFilters) {
        Class<?> rawClass = getRawClass(type);
        if (Objects.isNull(rawClass) || rawClass.isInterface()) {
            return emptyList();
        }
        List<Class<?>> allTypes = new LinkedList<>();
        allTypes.add(rawClass);
        allTypes.addAll(getAllSuperClasses(rawClass, NON_OBJECT_TYPE_FILTER));
        List<ParameterizedType> allGenericSuperClasses = allTypes.stream()
                .map(Class::getGenericSuperclass)
                .filter(TypeUtil::isParameterizedType)
                .map(ParameterizedType.class::cast)
                .collect(Collectors.toList());
        return filter(allGenericSuperClasses, typedFilters);
    }


    public static Class<?> getRawClass(Type type) {
        Type rawType = getRawType(type);
        if (isClass(rawType)) {
            return (Class) rawType;
        }
        return null;
    }

    public static boolean isClass(Type type) {
        return type instanceof Class;
    }

    public static Type getRawType(Type type) {
        if (isParameterizedType(type)) {
            return ((ParameterizedType) type).getRawType();
        }
        return type;
    }

    public static boolean isParameterizedType(Type type) {
        return type instanceof ParameterizedType;
    }


}
