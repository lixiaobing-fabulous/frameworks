package com.lxb.utils;

import static com.lxb.function.Streams.filter;
import static com.lxb.utils.ArrayUtil.isNotEmpty;
import static com.lxb.utils.ClassLoaderUtil.getClassLoader;
import static com.lxb.utils.CollectionUtils.asSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.reverse;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public class ClassUtil {
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP;
    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[L";

    static {
        Map<String, Class<?>> typeNamesMap = new HashMap<>(16);
        List<Class<?>> primitiveTypeNames = new ArrayList<>(16);
        primitiveTypeNames.addAll(asList(boolean.class, byte.class, char.class, double.class,
                float.class, int.class, long.class, short.class));
        primitiveTypeNames.addAll(asList(boolean[].class, byte[].class, char[].class, double[].class,
                float[].class, int[].class, long[].class, short[].class));
        for (Class<?> primitiveTypeName : primitiveTypeNames) {
            typeNamesMap.put(primitiveTypeName.getName(), primitiveTypeName);
        }
        PRIMITIVE_TYPE_NAME_MAP = unmodifiableMap(typeNamesMap);
    }

    public static Set<Class<?>> getAllInheritedTypes(Class<?> type, Predicate<Class<?>>... typeFilters) {
        // Add all super classes
        Set<Class<?>> types = new LinkedHashSet<>(getAllSuperClasses(type, typeFilters));
        // Add all interface classes
        types.addAll(getAllInterfaces(type, typeFilters));
        return unmodifiableSet(types);
    }

    public static Class<?> resolveClass(Object object) {
        return object instanceof Class ? (Class) object : object.getClass();
    }

    public static Set<Class<?>> getAllInterfaces(Class<?> type, Predicate<Class<?>>... interfaceFilters) {
        if (Objects.isNull(type) || type.isPrimitive()) {
            return emptySet();
        }
        List<Class<?>> allInterfaces = new LinkedList<>();
        Set<Class<?>> visited = new LinkedHashSet<>();
        Queue<Class<?>> queue = new LinkedList<>();

        visited.add(type);
        queue.add(type);
        while (!queue.isEmpty()) {
            Class<?> clazz = queue.poll();
            Class<?>[] interfaces = clazz.getInterfaces();
            if (isNotEmpty(interfaces)) {
                Arrays.stream(interfaces)
                        .filter(visited::add)
                        .forEach(cls -> {
                            allInterfaces.add(cls);
                            queue.add(cls);
                        });
            }
            getAllSuperClasses(clazz)
                    .stream()
                    .filter(visited::add)
                    .forEach(queue::add);

        }
        reverse(allInterfaces);
        return asSet(filter(allInterfaces, interfaceFilters));
    }


    public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {
        return getAllClasses(type, false, classFilters);
    }

    public static Set<Class<?>> getAllClasses(Class<?> type, boolean includeSelf, Predicate<Class<?>>... classFilters) {
        if (Objects.isNull(type) || type.isPrimitive()) {
            return emptySet();
        }
        List<Class<?>> allClasses = new LinkedList<>();
        Class<?> superclass = type.getSuperclass();
        while (Objects.nonNull(superclass)) {
            allClasses.add(superclass);
            superclass = superclass.getSuperclass();
        }
        reverse(allClasses);
        if (includeSelf) {
            allClasses.add(type);
        }
        List<Class<?>> classes = filter(allClasses, classFilters);
        return asSet(classes);
    }


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

    public static Class<?> resolveClass(String className, ClassLoader classLoader) {
        Class<?> targetClass = null;
        try {
            targetClass = forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
        }
        return targetClass;
    }

    public static Class<?> forName(String name, ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayMarker == 0) {
                elementClassName = name
                        .substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
            } else if (name.startsWith("[")) {
                elementClassName = name.substring(1);
            }
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = (Class<?>) PRIMITIVE_TYPE_NAME_MAP.get(name);
        }
        return result;
    }
}
