package com.lxb.rpc.util;



import com.lxb.rpc.exception.CreationException;
import com.lxb.rpc.exception.MethodOverloadException;
import com.lxb.rpc.exception.ReflectionException;
import com.lxb.rpc.util.thread.IDLConverter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Title: 类型转换工具类<br>
 * <p/>
 * Description: 调用端时将类描述转换为字符串传输。服务端将字符串转换为具体的类<br>
 * <pre>
 *     保证传递的时候值为可阅读格式，而不是jvm格式（[Lxxx;）：
 *         普通：java.lang.String、java.lang.String[]
 *         基本类型：int、int[]
 *         匿名类：io.joyrpc.Xxx$1、io.joyrpc.Xxx$1[]
 *         本地类：io.joyrpc.Xxx$1Local、io.joyrpc.Xxx$1Local[]
 *         成员类：io.joyrpc.Xxx$Member、io.joyrpc.Xxx$Member[]
 *         内部类：io.joyrpc.Inner、io.joyrpc.Inner[]
 *     同时Class.forName的时候又会解析出Class。
 *     </pre>
 * <p>
 */
public class ClassUtils {

    /**
     * 分静态和透明字段
     */
    public final static Predicate<Field> NONE_STATIC_TRANSIENT = (o) -> {
        int mod = o.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isTransient(mod);
    };

    protected final static Map<String, Class<?>> forNames = new ConcurrentHashMap<>(5000);

    /**
     * String-->Class 缓存，指定大小
     */
    protected final static Map<String, Class<?>> nameTypes = new ConcurrentHashMap<>(5000);

    /**
     * String-->Class 缓存，指定大小
     */
    protected final static Map<Class<?>, String> canonicalNames = new ConcurrentHashMap<>(5000);

    /**
     * 类的元数据
     */
    protected final static Map<Class<?>, ClassMeta> classMetas = new ConcurrentHashMap<>(5000);

    protected static Map<Class, Optional<ThrowableCreation>> throwableCreations = new ConcurrentHashMap<>();

    static {
        //这些类型不能用类加载器加载
        nameTypes.put("void", void.class);
        nameTypes.put("boolean", boolean.class);
        nameTypes.put("byte", byte.class);
        nameTypes.put("char", char.class);
        nameTypes.put("double", double.class);
        nameTypes.put("float", float.class);
        nameTypes.put("int", int.class);
        nameTypes.put("long", long.class);
        nameTypes.put("short", short.class);
        forNames.putAll(nameTypes);
    }

    /**
     * 是否是Java内置的类
     *
     * @param clazz
     * @return
     */
    public static boolean isJavaClass(final Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isPrimitive()) {
            return true;
        }
        String name = clazz.getName();
        int length = name.length();
        return length >= 5 && name.startsWith("java") && (name.charAt(4) == '.' || length >= 6 && name.startsWith("x.", 4));
    }

    /**
     * 是否默认类型
     *
     * @param clazz     类型
     * @param predicate 断言
     * @return the boolean
     */
    public static boolean isPrimitive(final Class<?> clazz, final Predicate<Class> predicate) {
        return clazz != null && (clazz.isPrimitive() || (predicate != null && predicate.test(clazz)));
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param clazzName  类名
     * @param methodName 方法名
     * @param argsType   参数列表
     * @return Method对象
     * @throws ClassNotFoundException 如果指定的类加载器无法定位该类
     * @throws NoSuchMethodException  如果找不到匹配的方法
     */
    public static Method getPublicMethod(final String clazzName, final String methodName, final String[] argsType) throws ClassNotFoundException, NoSuchMethodException {
        if (clazzName == null) {
            return null;
        }
        return getPublicMethod(forName(clazzName), methodName, argsType);
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param className  类名
     * @param methodName 方法名
     * @param sign       签名
     * @return Method对象
     * @throws MethodOverloadException 方法重载异常
     * @throws ClassNotFoundException  如果指定的类加载器无法定位该类
     * @throws NoSuchMethodException   如果找不到匹配的方法
     */
    public static Method getPublicMethod(final String className, final String methodName, final int sign) throws ClassNotFoundException, NoSuchMethodException {
        return className == null || methodName == null ? null : getPublicMethod(forName(className), methodName, sign);
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param sign       签名
     * @return Method对象
     * @throws NoSuchMethodException   如果找不到匹配的方法
     * @throws MethodOverloadException 方法重载异常
     */
    public static Method getPublicMethod(final Class<?> clazz, final String methodName, final int sign) throws NoSuchMethodException {
        return clazz == null || methodName == null ? null : getClassMeta(clazz).getMethod(methodName, sign);
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param argsType   参数列表
     * @return Method对象
     * @throws NoSuchMethodException 如果找不到匹配的方法
     */
    public static Method getPublicMethod(final Class<?> clazz, final String methodName, final String[] argsType) throws NoSuchMethodException {
        return clazz == null || methodName == null ? null : getClassMeta(clazz).getMethod(methodName, signMethod(methodName, argsType));
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param className  类
     * @param methodName 方法名
     * @return Method对象
     * @throws ClassNotFoundException  类找不到
     * @throws NoSuchMethodException   如果找不到匹配的方法
     * @throws MethodOverloadException 有方法重载异常
     */
    public static Method getPublicMethod(final String className, final String methodName) throws ClassNotFoundException, NoSuchMethodException, MethodOverloadException {
        if (className == null) {
            return null;
        }
        return getPublicMethod(forName(className), methodName);
    }

    /**
     * 获取唯一的指定名称的公共非静态方法，过滤掉了Object类型的方法
     *
     * @param clazz      类
     * @param methodName 方法名
     * @return Method对象
     * @throws NoSuchMethodException   如果找不到匹配的方法
     * @throws MethodOverloadException 有方法重载异常
     */
    public static Method getPublicMethod(final Class<?> clazz, final String methodName)
            throws NoSuchMethodException, MethodOverloadException {
        return clazz == null || methodName == null ? null : getClassMeta(clazz).getMethod(methodName);
    }

    /**
     * 获取IDL方法信息
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param function   IDLType函数
     * @return IDL方法信息
     * @throws NoSuchMethodException   如果找不到匹配的方法
     * @throws MethodOverloadException 有方法重载异常
     */
    public static IDLMethod getPublicMethod(final Class<?> clazz,
                                            final String methodName,
                                            final BiFunction<Class<?>, Method, IDLMethodDesc> function)
            throws NoSuchMethodException, MethodOverloadException {
        return clazz == null || methodName == null ? null : getClassMeta(clazz).getMethodMeta().getMethod(methodName, function);
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param clazz      类
     * @param methodName 方法名
     * @return Method对象
     * @throws NoSuchMethodException 如果找不到匹配的方法
     */
    public static Collection<Method> getPublicMethods(final Class<?> clazz, final String methodName) throws NoSuchMethodException {
        return clazz == null || methodName == null ? null : getClassMeta(clazz).getMethods(methodName);
    }

    /**
     * 构建方法没有指定注解的对象集合
     *
     * @param iterable        迭代对象
     * @param methodName      方法名称
     * @param annotationClass 注解类
     * @param <T>             对象类型
     * @param <A>             注解
     * @return 对象集合
     */
    public static <T, A extends Annotation> List<T> withoutAnnotation(final Iterable<T> iterable, final String methodName, final Class<A> annotationClass) {
        List<T> result = null;
        Method method;
        for (T t : iterable) {
            try {
                method = getPublicMethod(t.getClass(), methodName);
                A annotation = method.getAnnotation(annotationClass);
                if (annotation == null) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(t);
                }
            } catch (NoSuchMethodException | MethodOverloadException e) {
            }
        }
        return result;
    }

    /**
     * 获取公共方法，过滤掉了Object类型的方法
     *
     * @param clazz 类
     * @return 公共方法列表
     */
    public static List<Method> getPublicMethod(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getMethods();
    }

    /**
     * 获取Getter
     *
     * @param clazz 类
     * @return getter
     */
    public static Map<String, Method> getGetter(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getMethodMeta().getter;
    }

    /**
     * 获取Setter
     *
     * @param clazz 类
     * @return setter
     */
    public static Map<String, Method> getSetter(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getMethodMeta().setter;
    }

    /**
     * 方法签名
     *
     * @param methodName 方法名称
     * @param types      参数
     * @return 签名
     */
    public static int signMethod(final String methodName, final String[] types) {
        return Arrays.hashCode(types);
    }

    /**
     * 方法签名
     *
     * @param methodName 方法名称
     * @param types      参数
     * @return 前面
     */
    public static int signMethod(final String methodName, final Class<?>[] types) {
        if (types == null) {
            return 0;
        }
        int sign = 1;
        for (Class<?> type : types) {
            sign = 31 * sign + (type == null ? 0 : type.getName().hashCode());
        }
        return sign;
    }

    /**
     * 方法签名
     *
     * @param method 方法
     * @return
     */
    public static int signMethod(final Method method) {
        if (method == null) {
            return 0;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return 0;
        }
        int sign = 1;
        for (Parameter parameter : parameters) {
            sign = 31 * sign + parameter.getType().getName().hashCode();
        }
        return sign;
    }

    /**
     * 得到当前ClassLoader
     *
     * @return ClassLoader
     */
    public static ClassLoader getCurrentClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl == null ? ClassLoader.getSystemClassLoader() : cl;
    }

    /**
     * 得到当前ClassLoader
     *
     * @param clazz 某个类
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            return loader;
        }
        if (clazz != null) {
            loader = clazz.getClassLoader();
            if (loader != null) {
                return loader;
            }
        }
        return ClassLoader.getSystemClassLoader();
    }

    /**
     * 根据类名加载Class
     *
     * @param className 类名
     * @return Class
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> forName(final String className) throws ClassNotFoundException {
        return forName(className, true, getCurrentClassLoader());
    }

    /**
     * 根据类名加载Class
     *
     * @param className  类名
     * @param initialize 是否初始化
     * @return Class
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> forName(final String className, final boolean initialize) throws ClassNotFoundException {
        return forName(className, initialize, getCurrentClassLoader());
    }

    /**
     * 根据类名加载Class
     *
     * @param className   类名
     * @param classLoader Classloader
     * @return Class
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> forName(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        return forName(className, true, classLoader);
    }

    /**
     * 根据类名加载Class
     *
     * @param className   类名
     * @param initialize  是否初始化
     * @param classLoader Classloader
     * @return Class
     * @throws ClassNotFoundException 找不到类
     */
    public static Class<?> forName(final String className, final boolean initialize, final ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> result = forNameQuiet(className, initialize, classLoader);
        if (result == null) {
            throw new ClassNotFoundException(className);
        }
        return result;
    }

    /**
     * 根据类名加载Class，不存在返回空
     *
     * @param className 类名
     * @return Class
     */
    public static Class<?> forNameQuiet(final String className) {
        return forNameQuiet(className, true, getCurrentClassLoader());
    }

    /**
     * 根据类名加载Class，不存在返回空
     *
     * @param className   类名
     * @param initialize  是否初始化
     * @param classLoader Classloader
     * @return Class
     */
    public static Class<?> forNameQuiet(final String className, final boolean initialize, final ClassLoader classLoader) {
        if (className == null) {
            return null;
        }
        Class<?> result = forNames.get(className);
        if (result == null) {
            //不存在的类不要缓存，否则会造成漏洞，大量的无效类把内存撑爆
            try {
                result = Class.forName(className, initialize, classLoader);
                forNames.putIfAbsent(className, result);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        return result;
    }

    /**
     * 根据类名加载Class，不存在返回空
     *
     * @param className 类名
     * @param function  函数
     * @return Class
     */
    public static Class<?> forName(final String className, final Function<String, Class<?>> function) {
        if (className == null) {
            return null;
        }
        Class<?> result = forNames.get(className);
        if (result == null) {
            result = function == null ? null : function.apply(className);
            if (result != null) {
                Class<?> old = forNames.putIfAbsent(className, result);
                if (old != null) {
                    result = old;
                }
            }
        }
        return result;
    }

    /**
     * 获取类元数据
     *
     * @param clazz 类
     * @return 类元数据
     */
    protected static ClassMeta getClassMeta(final Class<?> clazz) {
        return clazz == null ? null : classMetas.computeIfAbsent(clazz, ClassMeta::new);
    }

    /**
     * 获取类的泛型信息
     *
     * @param clazz 类
     * @return 泛型类
     */
    public static GenericClass getGenericClass(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getGenericClass();
    }

    /**
     * 获取类所在文件
     *
     * @param clazz 类
     * @return 类所在文件
     */
    public static String getCodeBase(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getCodeBase();
    }

    /**
     * 获取类的字段
     *
     * @param clazz 类
     * @return 字段
     */
    public static List<Field> getFields(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getFields();
    }

    /**
     * 获取类的字段
     *
     * @param clazz      类
     * @param predicate  断言
     * @param accessible 可访问标识
     * @return 字段
     */
    public static Field[] getFields(final Class<?> clazz, final Predicate<Field> predicate, final boolean accessible) {
        if (clazz == null) {
            return null;
        }
        //获取字段
        List<Field> fields = getClassMeta(clazz).getFields();
        if (predicate == null) {
            //没有断言
            if (accessible) {
                fields.forEach(o -> {
                    if (!o.isAccessible()) {
                        o.setAccessible(true);
                    }
                });
            }
            return fields.toArray(new Field[fields.size()]);
        } else {
            //有断言
            LinkedList<Field> results = new LinkedList<>();
            for (Field field : fields) {
                if (predicate.test(field)) {
                    if (accessible && !field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    results.add(field);
                }
            }
            return results.toArray(new Field[results.size()]);
        }

    }

    /**
     * 获取类的字段名映射
     *
     * @param clazz 类
     * @return 字段
     */
    public static Map<String, Field> getFieldNames(final Class<?> clazz) {
        return clazz == null ? null : getClassMeta(clazz).getFieldNames();
    }

    /**
     * 获取类的字段
     *
     * @param clazz 类
     * @param name  属性名
     * @return 字段
     */
    public static Field getField(final Class<?> clazz, final String name) {
        return clazz == null || name == null ? null : getClassMeta(clazz).getField(name);
    }

    /**
     * 获取初始值
     *
     * @param clazz 类型
     * @return 初始值
     */
    public static Object getInitialValue(final Class<?> clazz) {
        if (clazz == boolean.class) {
            return Boolean.FALSE;
        } else if (clazz == char.class) {
            return (char) 0;
        } else if (clazz == byte.class) {
            return (byte) 0;
        } else if (clazz == short.class) {
            return (short) 0;
        } else if (clazz == int.class) {
            return 0;
        } else if (clazz == long.class) {
            return 0L;
        } else if (clazz == float.class) {
            return 0.0f;
        } else if (clazz == double.class) {
            return 0.0d;
        }
        return null;
    }

    /**
     * 获取值
     *
     * @param clazz  类
     * @param name   名称
     * @param target 目标对象
     * @return 属性值
     * @throws ReflectionException
     */
    public static Object getValue(final Class<?> clazz, final String name, final Object target) throws ReflectionException {
        if (clazz == null || name == null || target == null) {
            return null;
        }
        ReflectAccessor accessor = getClassMeta(clazz).getAccessor(name);
        return accessor == null || !accessor.isReadable() ? null : accessor.get(target);
    }

    /**
     * 获取值
     *
     * @param clazz  类
     * @param field  字段
     * @param target 目标对象
     * @return 属性值
     * @throws ReflectionException
     */
    public static Object getValue(final Class<?> clazz, final Field field, final Object target) throws ReflectionException {
        if (clazz == null || field == null || target == null) {
            return null;
        }
        ReflectAccessor accessor = getClassMeta(clazz).getFieldAccessor(field);
        return accessor == null || !accessor.isReadable() ? null : accessor.get(target);
    }

    /**
     * 设置值
     *
     * @param clazz  类
     * @param name   字段
     * @param target 目标对象
     * @param value  属性值
     * @return
     * @throws ReflectionException
     */
    public static boolean setValue(final Class<?> clazz, final String name,
                                   final Object target, final Object value) throws ReflectionException {
        if (clazz == null || name == null) {
            return false;
        }
        ReflectAccessor accessor = getClassMeta(clazz).getAccessor(name);
        if (accessor != null && accessor.isWriteable()) {
            accessor.set(target, value);
            return true;
        }
        return false;
    }

    /**
     * 设置值
     *
     * @param target   目标对象
     * @param name     字段
     * @param function 属性值函数
     * @return
     * @throws ReflectionException 反射异常
     */
    public static boolean setValue(final Object target, final String name,
                                   final BiFunction<Class<?>, Type, Object> function) throws ReflectionException {
        if (target == null || name == null) {
            return false;
        }
        ReflectAccessor accessor = getClassMeta(target.getClass()).getAccessor(name);
        if (accessor != null && accessor.isWriteable()) {
            accessor.set(target, function);
            return true;
        }
        return false;
    }

    /**
     * 设置值
     *
     * @param target   目标对象
     * @param values   字段值
     * @param function 属性值函数
     * @throws ReflectionException 反射异常
     */
    public static void setValues(final Object target, final Map<?, ?> values,
                                 final TriFunction<Object, Class<?>, Type, Object> function) throws ReflectionException {
        if (target == null || values == null) {
            return;
        }
        ClassMeta meta = getClassMeta(target.getClass());
        ReflectAccessor accessor;
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() != null) {
                accessor = meta.getAccessor((String) entry.getKey());
                if (accessor != null && accessor.isWriteable()) {
                    accessor.set(target, (c, t) -> function.apply(entry.getValue(), c, t));
                }
            }
        }
    }

    /**
     * 设置值
     *
     * @param clazz  类
     * @param field  字段
     * @param target 目标对象
     * @param value  属性值
     * @throws ReflectionException 反射异常
     */
    public static boolean setValue(final Class<?> clazz, final Field field,
                                   final Object target, final Object value) throws ReflectionException {
        if (clazz == null || field == null) {
            return false;
        }
        ReflectAccessor accessor = getClassMeta(clazz).getFieldAccessor(field);
        if (accessor != null && accessor.isWriteable()) {
            accessor.set(target, value);
            return true;
        }
        return false;
    }

    /**
     * 获取构造函数
     *
     * @param type 类
     * @return 构造函数计划
     */
    public static List<Constructor<?>> getConstructors(final Class<?> type) {
        return type == null ? null : getClassMeta(type).getConstructors();
    }

    /**
     * 获取默认构造函数
     *
     * @param type 类
     * @return 默认构造函数
     */
    public static Constructor<?> getDefaultConstructor(final Class<?> type) {
        return type == null ? null : getClassMeta(type).getDefaultConstructor();
    }

    /**
     * 获取单个参数的构造函数
     *
     * @param type          目标类型
     * @param parameterType 参数类型
     * @return 字段
     */
    public static Constructor<?> getConstructor(final Class<?> type, final Class<?> parameterType) {
        return type == null ? null : getClassMeta(type).getConstructor(parameterType);
    }

    /**
     * 装箱
     *
     * @param clazz 类
     * @return 装箱后的类
     */
    public static Class<?> inbox(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        } else if (!clazz.isPrimitive()) {
            return clazz;
        } else if (int.class == clazz) {
            return Integer.class;
        } else if (double.class == clazz) {
            return Double.class;
        } else if (char.class == clazz) {
            return Character.class;
        } else if (boolean.class == clazz) {
            return Boolean.class;
        } else if (long.class == clazz) {
            return Long.class;
        } else if (float.class == clazz) {
            return Float.class;
        } else if (short.class == clazz) {
            return Short.class;
        } else if (byte.class == clazz) {
            return Byte.class;
        } else {
            return clazz;
        }
    }

    /**
     * 迭代父类
     *
     * @param clazz 类
     * @return 父类迭代器
     */
    public static Iterator<Class<?>> iterate(final Class<?> clazz) {
        return new SuperIterator(clazz);
    }

    /**
     * 迭代父类
     *
     * @param clazz     类
     * @param predicate 断言
     * @return 父类迭代器
     */
    public static Iterator<Class<?>> iterate(final Class<?> clazz, final Predicate<Class<?>> predicate) {
        return new SuperIterator(clazz, predicate);
    }

    /**
     * 实例化一个对象(只检测默认构造函数，其它不管）
     *
     * @param clazz 对象类
     * @param <T>   对象具体类
     * @return 对象实例
     * @throws CreationException 实例化异常
     */
    public static <T> T newInstance(final Class<T> clazz) throws CreationException {
        return clazz == null ? null : getClassMeta(clazz).newInstance();
    }

    /**
     * 获取IDL类型转换函数
     *
     * @param clazz 对象类
     * @return 对象实例
     * @throws CreationException 实例化异常
     */
    public static IDLConverter getConversion(final Class<?> clazz) throws CreationException {
        ClassMeta meta = getClassMeta(clazz);
        return meta == null ? null : meta.getConversion();
    }

    /**
     * 构建异常信息
     *
     * @param exClass    异常类
     * @param message    错误信息
     * @param cause      原因
     * @param stackTrace 堆栈
     * @return
     * @throws CreationException
     */
    public static Throwable createException(final Class<?> exClass, final String message, final Throwable cause, StackTraceElement[] stackTrace) throws CreationException {
        Throwable result = null;
        if (exClass == null) {
            result = new Exception(message, cause);
        } else {
            if (!Throwable.class.isAssignableFrom(exClass)) {
                throw new CreationException("type not match, not Throwable. " + exClass.getName());
            }
            Optional<ThrowableCreation> optional = throwableCreations.computeIfAbsent(exClass, o -> {
                Constructor<?> constructor0 = null;
                Constructor<?> constructor1 = null;
                Constructor<?> constructor2 = null;
                Constructor<?> constructorNull = null;
                Class<?>[] types;
                boolean nullable;
                //遍历构造函数
                for (Constructor<?> constructor : o.getConstructors()) {
                    types = constructor.getParameterTypes();
                    if (types.length == 0) {
                        //无参构造函数
                        constructor0 = constructor;
                    } else if (types.length == 1 && types[0] == String.class) {
                        //只带消息参数的构造函数
                        constructor1 = constructor;
                    } else if (types.length == 2 && types[0] == String.class && types[1] == Throwable.class) {
                        //带消息和异常参数的构造函数
                        constructor2 = constructor;
                        break;
                    } else if (constructorNull == null) {
                        //参数都可以为空的构造函数
                        nullable = true;
                        for (Class<?> type : types) {
                            if (type.isPrimitive()) {
                                nullable = false;
                                break;
                            }
                        }
                        if (nullable) {
                            constructorNull = constructor;
                        }
                    }
                }

                if (constructor2 != null) {
                    return Optional.of(new ThrowableConstructor2(constructor2));
                } else if (constructor1 != null) {
                    return Optional.of(new ThrowableConstructor1(constructor1));
                } else if (constructor0 != null) {
                    return Optional.of(new ThrowableConstructor0(constructor0));
                } else if (constructorNull != null) {
                    return Optional.of(new ThrowableConstructorNull(constructorNull));
                }

                return Optional.empty();
            });

            ThrowableCreation instantiation = optional.get();
            try {

                result = instantiation == null ? new Exception(message, cause) : instantiation.newInstance(message, cause);
            } catch (Exception e) {
                throw new CreationException("create instance error", e);
            }
        }
        if (stackTrace != null) {
            result.setStackTrace(stackTrace);
        }
        return result;

    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pkName 包名
     * @return
     */
    public static Set<Class<?>> scan(final String pkName) throws IOException {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageName = pkName;
        String packageDir = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> dirs = classLoader.getResources(packageDir);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                scanByFile(classLoader, packageName, filePath, recursive, classes);
            } else if ("jar".equals(protocol)) {
                // 如果是jar包文件
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.charAt(0) == '/') {
                        // 如果是以/开头的,获取后面的字符串
                        name = name.substring(1);
                    }
                    if (name.startsWith(packageDir)) {
                        int index = name.lastIndexOf('/');
                        // 如果以"/"结尾 是一个包
                        if (index != -1) {
                            // 获取包名 把"/"替换成"."
                            packageName = name.substring(0, index).replace('/', '.');
                        }
                        if ((index != -1) || recursive) {
                            if (name.endsWith(".class") && !entry.isDirectory()) {
                                try {
                                    classes.add(Class.forName(packageName + '.' + name.substring(packageName.length() + 1, name.length() - 6)));
                                } catch (ClassNotFoundException e) {
                                }
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param classLoader 类加载器
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive   递归标识
     * @param classes     类集合
     */
    protected static void scanByFile(ClassLoader classLoader,
                                     String packageName,
                                     String packagePath,
                                     final boolean recursive,
                                     Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                scanByFile(classLoader, packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                try {
                    classes.add(classLoader.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }

    /**
     * 根据标准名称获取类型
     *
     * @param canonicalNames 标准名称数组
     * @return Class[]
     * @throws ClassNotFoundException 类没有找到异常
     */
    public static Class[] getClasses(final String[] canonicalNames) throws ClassNotFoundException {
        if (canonicalNames == null || canonicalNames.length == 0) {
            return new Class[0];
        } else {
            Class[] classes = new Class[canonicalNames.length];
            for (int i = 0; i < canonicalNames.length; i++) {
                classes[i] = getClass(canonicalNames[i]);
            }
            return classes;
        }
    }

    /**
     * 根据标准名称获取类型，如果不存在，抛出运行时异常
     *
     * @param canonicalNames 标准名称数组
     * @param function       异常转换函数
     * @return Class[]
     * @throws RuntimeException 运行时异常
     */
    public static Class[] getClasses(final String[] canonicalNames, final Function<ClassNotFoundException, RuntimeException> function)
            throws RuntimeException {
        if (canonicalNames == null || canonicalNames.length == 0) {
            return new Class[0];
        } else {
            Class[] classes = new Class[canonicalNames.length];
            for (int i = 0; i < canonicalNames.length; i++) {
                classes[i] = getClass(canonicalNames[i], function);
            }
            return classes;
        }
    }

    /**
     * 根据标准名称获取类型，如果不存在，抛出运行时异常
     *
     * @param canonicalName 标准名称
     * @param function      异常转换函数
     * @return Class 类
     * @throws RuntimeException 运行时异常
     */
    public static Class<?> getClass(final String canonicalName, final Function<ClassNotFoundException, RuntimeException> function) throws RuntimeException {
        try {
            return getClass(canonicalName);
        } catch (ClassNotFoundException e) {
            throw function != null ? function.apply(e) : new RuntimeException(e);
        }
    }

    /**
     * 根据标准名称获取类型
     *
     * @param canonicalName 标准名称
     * @return Class
     * @throws ClassNotFoundException
     */
    public static Class<?> getClass(final String canonicalName) throws ClassNotFoundException {
        if (canonicalName == null || canonicalName.isEmpty()) {
            return null;
        }
        Class<?> result = nameTypes.get(canonicalName);
        if (result == null) {
            switch (canonicalName) {
                case "void":
                    result = void.class;
                    break;
                case "boolean":
                    result = boolean.class;
                    break;
                case "byte":
                    result = byte.class;
                    break;
                case "char":
                    result = char.class;
                    break;
                case "double":
                    result = double.class;
                    break;
                case "float":
                    result = float.class;
                    break;
                case "int":
                    result = int.class;
                    break;
                case "long":
                    result = long.class;
                    break;
                case "short":
                    result = short.class;
                    break;
                default:
                    //不存在的不要缓存，防止缓存大量的无效类，撑爆内存
                    result = forName(canonicalNameToJvmName(canonicalName));
            }
            nameTypes.putIfAbsent(canonicalName, result);
        }
        return result;
    }

    /**
     * 判断返回值是否是CompletableFuture
     *
     * @param clazz 类
     * @return 判断返回值是否是CompletableFuture
     */
    public static boolean isReturnFuture(final Class<?> clazz, final Method method) {
        return CompletableFuture.class == method.getReturnType();
    }

    /**
     * 根据类型获取标准名称
     *
     * @param types 类型
     * @return 对象描述
     */
    public static String[] getCanonicalNames(final Class[] types) {
        if (types == null || types.length == 0) {
            return new String[0];
        } else {
            String[] strings = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                strings[i] = getCanonicalName(types[i]);
            }
            return strings;
        }
    }

    /**
     * 获取标准名称
     *
     * @param clazz 类型
     * @return 对象
     * @see #getClass(String)
     */
    public static String getCanonicalName(final Class clazz) {
        return clazz == null ? null : canonicalNames.computeIfAbsent(clazz, o -> o.getTypeName());
    }

    /**
     * 标准名称转换成JVM名称
     *
     * @param name 标准名称
     * @return JVM名称
     */
    protected static String canonicalNameToJvmName(String name) {
        boolean array = name.endsWith("[]");
        if (array) {
            String t = ""; // 计数，看上几维数组
            while (array) {
                name = name.substring(0, name.length() - 2);
                t += "[";
                array = name.endsWith("[]");
            }
            switch (name) {
                case "void":
                    name = t + "V";
                    break;
                case "boolean":
                    name = t + "Z";
                    break;
                case "byte":
                    name = t + "B";
                    break;
                case "char":
                    name = t + "C";
                    break;
                case "double":
                    name = t + "D";
                    break;
                case "float":
                    name = t + "F";
                    break;
                case "int":
                    name = t + "I";
                    break;
                case "long":
                    name = t + "J";
                    break;
                case "short":
                    name = t + "S";
                    break;
                default:
                    name = t + "L" + name + ";";
                    break;
            }
        }
        return name;
    }

    /**
     * 获取类型描述，兼容Dubbo.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param c class.
     * @return desc.
     */
    public static String getDesc(final Class<?> c) {
        return getDesc(c, new StringBuilder());
    }

    /**
     * get class desc.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param clazz   class.
     * @param builder String builder.
     * @return desc.
     */
    protected static String getDesc(final Class<?> clazz, final StringBuilder builder) {
        Class<?> c = clazz;
        while (c.isArray()) {
            builder.append('[');
            c = c.getComponentType();
        }
        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) {
                builder.append('V');
            } else if ("boolean".equals(t)) {
                builder.append('Z');
            } else if ("byte".equals(t)) {
                builder.append('B');
            } else if ("char".equals(t)) {
                builder.append('C');
            } else if ("double".equals(t)) {
                builder.append('D');
            } else if ("float".equals(t)) {
                builder.append('F');
            } else if ("int".equals(t)) {
                builder.append('I');
            } else if ("long".equals(t)) {
                builder.append('J');
            } else if ("short".equals(t)) {
                builder.append('S');
            }
        } else {
            builder.append('L');
            builder.append(c.getName().replace('.', '/'));
            builder.append(';');
        }
        return builder.toString();
    }

    /**
     * 获取类型数组描述，兼容Dubbo.
     * [int.class, boolean[].class, Object.class] => "I[ZLjava/lang/Object;"
     *
     * @param classes class array.
     * @return desc.
     */
    public static String getDesc(final Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(64);
        for (Class<?> c : classes) {
            getDesc(c, builder);
        }
        return builder.toString();
    }

    /**
     * 获取数组类型的最终单元class
     *
     * @param clazz
     * @return
     */
    public static Class<?> getComponentType(Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    /**
     * 类元数据
     */
    protected static class ClassMeta {
        /**
         * 类型
         */
        protected Class<?> type;
        /**
         * 字段元数据
         */
        protected volatile FieldMeta fieldMeta;
        /**
         * 构造函数元数据
         */
        protected volatile ConstructorMeta constructorMeta;
        /**
         * 方法元数据
         */
        protected volatile MethodMeta methodMeta;
        /**
         * 字段访问器
         */
        protected volatile Map<Field, ReflectAccessor> fieldAccessors;
        /**
         * 属性访问器
         */
        protected volatile Map<String, ReflectAccessor> propertyAccessors;
        /**
         * 泛型信息
         */
        protected volatile GenericClass genericClass;
        /**
         * jar文件
         */
        protected volatile Optional<String> codebase;

        protected volatile IDLConverter conversion;

        /**
         * 构造函数
         *
         * @param type 类型
         */
        public ClassMeta(final Class<?> type) {
            this.type = type;
        }

        /**
         * 获取构造函数元数据，延迟加载
         *
         * @return 构造函数元数据
         */
        protected ConstructorMeta getConstructorMeta() {
            if (constructorMeta == null) {
                synchronized (this) {
                    constructorMeta = new ConstructorMeta(type);
                }
            }
            return constructorMeta;
        }

        /**
         * 获取字段元数据
         */
        protected FieldMeta getFieldMeta() {
            if (fieldMeta == null) {
                synchronized (this) {
                    if (fieldMeta == null) {
                        fieldMeta = new FieldMeta(type);
                    }
                }
            }
            return fieldMeta;
        }

        /**
         * 获取方法元数据
         *
         * @return 方法元数据
         */
        protected MethodMeta getMethodMeta() {
            if (methodMeta == null) {
                FieldMeta fieldMeta = getFieldMeta();
                synchronized (this) {
                    if (methodMeta == null) {
                        methodMeta = new MethodMeta(type, name -> fieldMeta.getField(name) != null);
                    }
                }
            }
            return methodMeta;
        }

        /**
         * 获取泛型信息
         */
        public GenericClass getGenericClass() {
            if (genericClass == null) {
                synchronized (this) {
                    if (genericClass == null) {
                        genericClass = new GenericClass(type);
                    }
                }
            }
            return genericClass;
        }

        /**
         * 获取字段
         *
         * @return 字段集合
         */
        public List<Field> getFields() {
            return getFieldMeta().getFields();
        }

        /**
         * 获取字段名称
         *
         * @return 字段映射
         */
        public Map<String, Field> getFieldNames() {
            return getFieldMeta().getFieldNames();
        }

        /**
         * 根据名称获取字段
         *
         * @param name 名称
         * @return 字段
         */
        public Field getField(final String name) {
            return getFieldMeta().getField(name);
        }

        /**
         * 根据属性名称获取访问器
         *
         * @param name 名称
         * @return 反射访问器
         */
        protected ReflectAccessor getAccessor(final String name) {
            if (propertyAccessors == null) {
                synchronized (this) {
                    if (propertyAccessors == null) {
                        propertyAccessors = new ConcurrentHashMap<>(fieldMeta != null ? fieldMeta.fields.size() : 20);
                    }
                }
            }
            ReflectAccessor result = propertyAccessors.get(name);
            if (result == null) {
                MethodMeta meta = getMethodMeta();
                Field field = getField(name);
                GenericType genericType = getGenericClass().get(field);
                Method getter = meta.getGetter(name);
                Method setter = meta.getSetter(name);
                if (field == null && getter == null && setter == null) {
                    //不缓存，防止外部调用注入过多的无效属性造成OOM
                    return null;
                }
                return propertyAccessors.computeIfAbsent(name, o -> new ReflectAccessor(field, getter, setter, genericType));
            }
            return result;
        }

        /**
         * 获取字段访问器
         *
         * @param field 字段
         * @return 反射访问器
         */
        protected ReflectAccessor getFieldAccessor(final Field field) {
            if (fieldAccessors == null) {
                synchronized (this) {
                    if (fieldAccessors == null) {
                        fieldAccessors = new ConcurrentHashMap<>(fieldMeta != null ? fieldMeta.fields.size() : 20);
                    }
                }
            }
            return field == null ? null : fieldAccessors.computeIfAbsent(field, k -> {
                Class<?> fieldType = field.getType();
                String name = k.getName();
                MethodMeta meta = getMethodMeta();
                Method getter = meta.getGetter(name);
                Method setter = meta.getSetter(name);

                getter = getter != null && getter.getReturnType().equals(fieldType) ? getter : null;
                setter = setter != null && setter.getParameters()[0].getType().equals(fieldType) ? setter : null;

                GenericType genericType = getGenericClass().get(field);

                return new ReflectAccessor(field, getter, setter, genericType);

            });
        }

        public Class<?> getType() {
            return type;
        }

        /**
         * 获取重载方法
         *
         * @param name 名称
         * @return 重载方法
         */
        public OverloadMethod getOverloadMethod(final String name) {
            return getMethodMeta().getOverloadMethod(name);
        }

        /**
         * 根据签名获取方法
         *
         * @param name 名称
         * @param sign 签名
         * @return 方法
         * @throws NoSuchMethodException 方法不存在异常
         */
        public Method getMethod(final String name, final int sign) throws NoSuchMethodException {
            return getMethodMeta().getMethod(name, sign);
        }

        /**
         * 获取单一方法
         *
         * @param name 名称
         * @return 方法
         * @throws NoSuchMethodException   方法不存在异常
         * @throws MethodOverloadException 方法重载异常
         */
        public Method getMethod(final String name) throws NoSuchMethodException, MethodOverloadException {
            return getMethodMeta().getMethod(name);
        }

        /**
         * 获取重载的方法列表
         *
         * @param name 名称
         * @return 方法
         * @throws NoSuchMethodException 方法不存在异常
         */
        public Collection<Method> getMethods(final String name) throws NoSuchMethodException {
            return getMethodMeta().getMethods(name);
        }

        /**
         * 获取方法列表
         *
         * @return 方法列表
         */
        public List<Method> getMethods() {
            return getMethodMeta().getMethods();
        }

        /**
         * 获取单一参数的构造函数
         *
         * @param type 参数类型
         * @return 指定类型参数的构造函数
         */
        public Constructor<?> getConstructor(final Class type) {
            return type == null ? null : getConstructorMeta().getConstructor(type);
        }

        /**
         * 获取默认构造函数
         *
         * @return 默认构造函数
         */
        public Constructor<?> getDefaultConstructor() {
            return getConstructorMeta().getDefaultConstructor();
        }

        /**
         * 获取所有构造函数
         *
         * @return 构造函数集合
         */
        public List<Constructor<?>> getConstructors() {
            return getConstructorMeta().getConstructors();
        }

        /**
         * 得到类所在地址，可以是文件，也可以是jar包
         *
         * @return the code base
         */
        public String getCodeBase() {
            if (codebase == null) {
                synchronized (this) {
                    if (codebase == null) {
                        String file = null;
                        ProtectionDomain domain = type.getProtectionDomain();
                        if (domain != null) {
                            CodeSource source = domain.getCodeSource();
                            if (source != null) {
                                URL location = source.getLocation();
                                if (location != null) {
                                    file = location.getFile();
                                }
                            }
                        }
                        codebase = Optional.ofNullable(file);
                    }
                }
            }
            return codebase.orElse(null);

        }

        /**
         * 实例化
         *
         * @param <T>
         * @return 实例
         * @throws CreationException
         */
        public <T> T newInstance() throws CreationException {
            return getConstructorMeta().newInstance();
        }

        /**
         * 获取IDL转换函数
         *
         * @return IDL转换函数
         */
        public IDLConverter getConversion() {
            if (conversion == null) {
                synchronized (this) {
                    if (conversion == null) {
                        conversion = new IDLConverter(this::toWrapper, this::toParameters);
                    }
                }
            }
            return conversion;
        }

        /**
         * 包装对象转换成参数
         *
         * @param target 包装对象
         * @return 参数数组
         */
        protected Object[] toParameters(final Object target) {
            if (target instanceof IDLConversion) {
                return ((IDLConversion) target).toArgs();
            }
            List<Field> fields = getFields();
            Object[] result = new Object[fields.size()];
            ReflectAccessor accessor;
            int i = 0;
            for (Field field : fields) {
                accessor = getFieldAccessor(field);
                result[i++] = accessor == null || !accessor.isReadable() ? null : accessor.get(target);
            }
            return result;
        }

        /**
         * 参数转成包装对象
         *
         * @param args 参数数组
         * @return 包装对象
         */
        protected Object toWrapper(final Object[] args) {
            Object result = newInstance();
            if (result instanceof IDLConversion) {
                ((IDLConversion) result).toFields(args);
            } else {
                List<Field> fields = getFields();
                int i = 0;
                for (Field field : fields) {
                    ReflectAccessor accessor = getFieldAccessor(field);
                    if (accessor != null && accessor.isWriteable()) {
                        accessor.set(result, args[i++]);
                    }
                }
            }
            return result;
        }
    }

    /**
     * 字段元数据
     */
    protected static class FieldMeta {
        /**
         * 类型
         */
        protected Class<?> type;
        /**
         * 字段
         */
        protected List<Field> fields = new LinkedList<>();
        /**
         * 字段名称
         */
        protected Map<String, Field> fieldNames;

        public FieldMeta(Class<?> type) {
            this.type = type;
            //判断非基本类型，非数组，非接口
            if (!type.isPrimitive() && !type.isArray() && !type.isInterface()) {
                //迭代父类获取字段
                Iterator<Class<?>> iterator = iterate(type);
                while (iterator.hasNext()) {
                    for (Field field : iterator.next().getDeclaredFields()) {
                        fields.add(field);
                    }
                }
                fieldNames = new HashMap<>(fields.size());
                for (Field field : fields) {
                    fieldNames.put(field.getName(), field);
                }
            } else {
                fieldNames = new HashMap<>();
            }
        }

        public List<Field> getFields() {
            return fields;
        }

        public Map<String, Field> getFieldNames() {
            return fieldNames;
        }

        /**
         * 获取字段
         *
         * @param name
         * @return
         */
        public Field getField(final String name) {
            return name == null ? null : fieldNames.get(name);
        }
    }

    /**
     * 方法元数据
     */
    protected static class MethodMeta {
        /**
         * 类型
         */
        protected Class<?> type;
        /**
         * 公共重载信息
         */
        protected Map<String, OverloadMethod> overloadMethods;
        /**
         * 读
         */
        protected Map<String, Method> getter;
        /**
         * 写方法
         */
        protected Map<String, Method> setter;

        /**
         * 公共方法
         */
        protected List<Method> methods;

        /**
         * 构造函数
         *
         * @param type      类型
         * @param predicate 是否是字段
         */
        public MethodMeta(Class<?> type, Predicate<String> predicate) {
            this.type = type;
            if (!type.isPrimitive() && !type.isArray()) {
                Method[] publicMethods = type.getMethods();
                overloadMethods = new HashMap<>(publicMethods.length);
                setter = new HashMap<>(publicMethods.length / 2);
                getter = new HashMap<>(publicMethods.length / 2);
                methods = new ArrayList<>(publicMethods.length);
                String name;
                for (Method method : publicMethods) {
                    if (!method.getDeclaringClass().equals(Object.class)) {
                        overloadMethods.computeIfAbsent(method.getName(), k -> new OverloadMethod(type, k)).add(method);
                        methods.add(method);
                        //getter和setter方法，过滤掉静态方法
                        if (!Modifier.isStatic(method.getModifiers())) {
                            name = method.getName();
                            if (name.startsWith("get")) {
                                if (name.length() > 3 && method.getParameterCount() == 0
                                        && void.class != method.getReturnType()) {
                                    name = name.substring(3, 4).toLowerCase() + name.substring(4);
                                    if ((predicate == null || predicate.test(name))) {
                                        getter.put(name, method);
                                    }
                                }
                            } else if (name.startsWith("is")) {
                                if (name.length() > 2 && method.getParameterCount() == 0
                                        && boolean.class == method.getReturnType()) {
                                    name = name.substring(2, 3).toLowerCase() + name.substring(3);
                                    if ((predicate == null || predicate.test(name))) {
                                        getter.put(name, method);
                                    }
                                }
                            } else if (name.startsWith("set")) {
                                if (name.length() > 3 && method.getParameterCount() == 1) {
                                    name = name.substring(3, 4).toLowerCase() + name.substring(4);
                                    if ((predicate == null || predicate.test(name))) {
                                        setter.put(name, method);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                overloadMethods = new HashMap<>(0);
                setter = new HashMap<>(0);
                getter = new HashMap<>(0);
                methods = new ArrayList<>(0);
            }
        }

        /**
         * 获取重载方法
         *
         * @param name
         * @return
         */
        public OverloadMethod getOverloadMethod(final String name) {
            return overloadMethods.get(name);
        }

        public List<Method> getMethods() {
            return methods;
        }

        public Method getSetter(final String name) {
            return setter.get(name);
        }

        public Method getGetter(final String name) {
            return getter.get(name);
        }

        /**
         * 根据签名获取方法
         *
         * @param name 名称
         * @param sign 签名
         * @return 方法
         * @throws NoSuchMethodException
         */
        public Method getMethod(final String name, final int sign) throws NoSuchMethodException {
            OverloadMethod method = getOverloadMethod(name);
            return method == null ? null : method.getMethod(sign);
        }

        /**
         * 获取单一方法信息
         *
         * @param name 名称
         * @return 方法信息
         * @throws NoSuchMethodException
         * @throws MethodOverloadException
         */
        public MethodInfo getMethodInfo(final String name) throws NoSuchMethodException, MethodOverloadException {
            OverloadMethod method = getOverloadMethod(name);
            if (method == null) {
                throw new NoSuchMethodException(String.format("Method is not found. %s", name));
            }
            return method.get();
        }

        /**
         * 获取单一方法
         *
         * @param name 名称
         * @return 方法
         * @throws NoSuchMethodException
         * @throws MethodOverloadException
         */
        public Method getMethod(final String name) throws NoSuchMethodException, MethodOverloadException {
            OverloadMethod method = getOverloadMethod(name);
            if (method == null) {
                throw new NoSuchMethodException(String.format("Method is not found. %s", name));
            }
            return method.getMethod();
        }

        /**
         * 获取IDL方法信息
         *
         * @param name     方法名称
         * @param function IDLType函数
         * @return
         * @throws NoSuchMethodException
         * @throws MethodOverloadException
         */
        public IDLMethod getMethod(final String name, final BiFunction<Class<?>, Method, IDLMethodDesc> function) throws
                NoSuchMethodException, MethodOverloadException {
            OverloadMethod method = getOverloadMethod(name);
            if (method == null) {
                throw new NoSuchMethodException(String.format("Method is not found. %s", name));
            }
            MethodInfo info = method.get();
            return new IDLMethod(type, method.getMethod(), () -> info.getIDLType(function));
        }

        /**
         * 获取重载的方法列表
         *
         * @param name 名称
         * @return 方法集合
         * @throws NoSuchMethodException
         */
        public Collection<Method> getMethods(final String name) throws NoSuchMethodException {
            OverloadMethod method = getOverloadMethod(name);
            if (method == null) {
                throw new NoSuchMethodException(String.format("Method is not found. %s", name));
            }
            return method.getMethods();
        }
    }

    /**
     * 重载的方法，在同步块里面添加
     */
    protected static class OverloadMethod {
        /**
         * 类型
         */
        protected Class<?> clazz;
        /**
         * 名称
         */
        protected String name;
        /**
         * 第一个方法
         */
        protected MethodInfo first;
        /**
         * 多个方法的签名
         */
        protected Map<Integer, MethodInfo> signs;
        /**
         * 方法元数据
         */
        protected Map<Method, MethodInfo> metas;

        /**
         * 构造函数
         *
         * @param clazz 类
         * @param name  名称
         */
        public OverloadMethod(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        /**
         * 构造函数
         *
         * @param clazz  类
         * @param method 方法
         */
        public OverloadMethod(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.name = method.getName();
            this.first = new MethodInfo(clazz, method);
        }

        /**
         * 添加方法
         *
         * @param method 方法
         */
        protected void add(final Method method) {
            if (first == null) {
                first = new MethodInfo(clazz, method);
            } else {
                if (signs == null) {
                    signs = new HashMap<>(4);
                    //该方法内部调用，采用IdentityHashMap优化性能
                    metas = new IdentityHashMap<>(4);
                    signs.put(first.sign, first);
                    metas.put(first.method, first);
                }
                MethodInfo meta = new MethodInfo(clazz, method);
                signs.put(meta.sign, meta);
                metas.put(method, meta);
            }
        }

        /**
         * 根据方法获取元数据
         *
         * @param method
         * @return
         */
        public MethodInfo get(final Method method) {
            return metas != null ? metas.get(method) : (first.method == method ? first : null);
        }

        /**
         * 根据签名获取方法元数据
         *
         * @param sign
         * @return
         */
        public MethodInfo get(final int sign) {
            //如果只有一个方法，则不判断签名
            return metas == null ? first : signs.get(sign);
        }

        /**
         * 根据签名获取方法元数据
         *
         * @return
         * @throws MethodOverloadException
         */
        public MethodInfo get() throws MethodOverloadException {
            //如果只有一个方法，则不判断签名
            if (signs == null) {
                return first;
            }
            throw new MethodOverloadException(String.format("Method %s is overload.", name));
        }

        /**
         * 获取重载的方法列表
         *
         * @return
         */
        public Collection<Method> getMethods() {
            return metas.keySet();
        }

        /**
         * 获取单一方法
         *
         * @return
         * @throws MethodOverloadException
         */
        public Method getMethod() throws MethodOverloadException {
            if (signs == null) {
                return first.method;
            }
            throw new MethodOverloadException(String.format("Method %s is overload.", name));
        }

        /**
         * 根据签名获取方法
         *
         * @param sign
         * @return
         * @throws NoSuchMethodException
         */
        public Method getMethod(final int sign) throws NoSuchMethodException {
            MethodInfo meta = get(sign);
            if (meta == null) {
                throw new NoSuchMethodException(String.format("Method is not found. name=%s,sign=%d", name, sign));
            }
            return meta.method;
        }

        /**
         * 获取指定类型的单一参数的方法
         *
         * @param parameterType
         * @return
         */
        public Method getMethod(final Class parameterType) {
            Parameter[] parameters;
            if (signs == null || signs.isEmpty()) {
                parameters = first.method.getParameters();
                if (parameters.length == 1 && parameters[0].getType().equals(parameterType)) {
                    return first.method;
                }
            } else {
                for (MethodInfo info : metas.values()) {
                    parameters = info.method.getParameters();
                    if (parameters.length == 1 && parameters[0].getType().equals(parameterType)) {
                        return info.method;
                    }
                }
            }
            return null;
        }
    }

    /**
     * 方法元数据
     */
    protected static class MethodInfo {
        /**
         * 类型
         */
        protected Class<?> clazz;
        /**
         * 方法
         */
        protected Method method;
        /**
         * 名称
         */
        protected String name;
        /**
         * 签名
         */
        protected int sign;
        /**
         * IDL类型
         */
        protected volatile IDLMethodDesc IDLType;

        /**
         * 构造函数
         *
         * @param clazz
         * @param method
         */
        public MethodInfo(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
            this.name = method.getName();
            this.sign = signMethod(method);
        }

        public Method getMethod() {
            return method;
        }

        public String getName() {
            return name;
        }

        public int getSign() {
            return sign;
        }

        /**
         * 获取方法类型
         *
         * @param function 函数
         * @return IDL类型
         */
        public IDLMethodDesc getIDLType(final BiFunction<Class<?>, Method, IDLMethodDesc> function) {
            if (IDLType == null) {
                if (function == null) {
                    return null;
                }
                synchronized (this) {
                    if (IDLType == null) {
                        IDLType = function.apply(clazz, method);
                    }
                }
            }
            return IDLType;
        }
    }

    /**
     * 构造函数
     */
    protected static class ConstructorMeta {
        /**
         * 类型
         */
        protected Class<?> type;
        /**
         * 单参数公开的构造函数
         */
        protected Map<Class<?>, Constructor<?>> singleConstructors = new HashMap<>(3);
        /**
         * 默认公开的构造函数
         */
        protected Constructor<?> defaultConstructor;
        /**
         * 默认单一参数构造函数
         */
        protected Constructor<?> defaultSingleConstructor;
        /**
         * 参数最小的构造函数
         */
        protected Constructor<?> minimumConstructor;
        /**
         * 构造函数
         */
        protected List<Constructor<?>> constructors = new LinkedList<>();

        /**
         * 构造函数
         *
         * @param type
         */
        public ConstructorMeta(Class type) {
            this.type = type;
            //判断是否是公共的具体实现类
            int modifiers = type.getModifiers();
            boolean concrete = !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers);
            Parameter[] parameters;
            int minimum = Integer.MAX_VALUE;
            for (Constructor<?> c : type.getDeclaredConstructors()) {
                constructors.add(c);
                if (concrete) {
                    parameters = c.getParameters();
                    //获取最少参数的构造函数
                    if (parameters.length < minimum) {
                        minimumConstructor = c;
                        minimum = parameters.length;
                    }
                    switch (parameters.length) {
                        case 0:
                            //默认函数
                            defaultConstructor = setAccessible(c);
                            break;
                        case 1:
                            //单个参数
                            defaultSingleConstructor = defaultSingleConstructor == null ? c : defaultSingleConstructor;
                            singleConstructors.put(inbox(parameters[0].getType()), setAccessible(c));
                            break;
                    }
                }
            }
            if (minimumConstructor != null) {
                minimumConstructor = (minimumConstructor == defaultConstructor || minimumConstructor == defaultSingleConstructor)
                        ? null : setAccessible(minimumConstructor);
            }
        }

        /**
         * 设置可以访问
         *
         * @param constructor
         */
        protected Constructor<?> setAccessible(final Constructor<?> constructor) {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor;
        }

        /**
         * 获取单一参数的构造函数
         *
         * @param type
         * @return
         */
        public Constructor<?> getConstructor(final Class type) {
            return type == null ? null : singleConstructors.get(type);
        }

        public Constructor<?> getDefaultConstructor() {
            return defaultConstructor;
        }

        public Constructor<?> getDefaultSingleConstructor() {
            return defaultSingleConstructor;
        }

        public List<Constructor<?>> getConstructors() {
            return constructors;
        }

        /**
         * 实例化
         *
         * @param <T>
         * @return
         * @throws CreationException
         */
        public <T> T newInstance() throws CreationException {
            try {
                if (type.isMemberClass() && !Modifier.isStatic(type.getModifiers())) {
                    if (defaultSingleConstructor != null) {
                        //内部类默认构造函数
                        return (T) defaultSingleConstructor.newInstance(new Object[]{null});
                    }
                } else if (defaultConstructor != null) {
                    //默认构造函数
                    return (T) defaultConstructor.newInstance();
                }
                if (minimumConstructor != null) {
                    //最小参数构造函数，构造默认参数
                    Object[] parameters = new Object[minimumConstructor.getParameterCount()];
                    int i = 0;
                    for (Class cl : minimumConstructor.getParameterTypes()) {
                        if (char.class == cl) {
                            parameters[i] = Character.MIN_VALUE;
                        } else if (boolean.class == cl) {
                            parameters[i] = false;
                        } else {
                            parameters[i] = cl.isPrimitive() ? 0 : null;
                        }
                    }
                    return (T) minimumConstructor.newInstance(parameters);
                }
                return null;
            } catch (Exception e) {
                throw new CreationException(String.format("Error occurs while instance class %s", type), e);
            }
        }
    }

    /**
     * 反射字段访问器
     */
    protected static class ReflectAccessor {
        // 字段
        protected Field field;
        // 获取方法
        protected Method getter;
        // 设置方法
        protected Method setter;
        // 字段泛型信息
        protected GenericType genericType;

        public ReflectAccessor(Field field, Method getter, Method setter, GenericType genericType) {
            this.field = field;
            this.getter = getter;
            this.setter = setter;
            this.genericType = genericType;
        }

        /**
         * 是否可写
         *
         * @return
         */
        public boolean isWriteable() {
            //有set方法 或 field存在 并且filed不是final的
            return (field != null && !Modifier.isFinal(field.getModifiers()))
                    || setter != null;
        }

        /**
         * 是否可读
         *
         * @return
         */
        public boolean isReadable() {
            return field != null && getter != null;
        }

        /**
         * 获取值
         *
         * @param target 目标对象
         * @return 值
         * @throws ReflectionException
         */
        public Object get(final Object target) throws ReflectionException {
            if (target == null) {
                return null;
            }
            try {
                if (getter != null) {
                    return getter.invoke(target);
                } else if (field != null) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    return field.get(target);
                }
                return null;
            } catch (Exception e) {
                throw new ReflectionException(e.getMessage(), e);
            }

        }

        /**
         * 设置值
         *
         * @param target 目标对象
         * @param value  值
         * @throws ReflectionException
         */
        public void set(final Object target, final Object value) throws ReflectionException {
            if (target == null) {
                return;
            }
            try {
                if (setter != null) {
                    setter.invoke(target, value);
                } else if (field != null) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(target, value);
                }
            } catch (Exception e) {
                throw new ReflectionException(e.getMessage(), e);
            }
        }

        /**
         * 设置值
         *
         * @param target   目标对象
         * @param function 函数
         * @throws ReflectionException
         */
        public void set(final Object target, final BiFunction<Class<?>, Type, Object> function) throws ReflectionException {
            if (target == null) {
                return;
            }
            try {
                if (setter != null) {
                    setter.invoke(target, function.apply(genericType.getType(), genericType.getGenericType()));
                } else if (field != null) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(target, function.apply(genericType.getType(), genericType.getGenericType()));
                }
            } catch (Exception e) {
                throw new ReflectionException(e.getMessage(), e);
            }
        }

    }

    /**
     * 实例化接口
     */
    @FunctionalInterface
    protected interface ThrowableCreation {

        /**
         * 构造
         *
         * @return
         * @throws CreationException
         */
        Throwable newInstance(String message, Throwable cause) throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException;
    }

    /**
     * 无参数构造函数实
     */
    protected static class ThrowableConstructor0 implements ThrowableCreation {

        protected Constructor constructor;

        public ThrowableConstructor0(final Constructor constructor) {
            this.constructor = constructor;
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
        }

        public Throwable newInstance(final String message, final Throwable cause) throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            return (Throwable) constructor.newInstance();
        }
    }

    /**
     * 参数为消息的构造函数
     */
    protected static class ThrowableConstructor1 extends ThrowableConstructor0 {

        public ThrowableConstructor1(Constructor constructor) {
            super(constructor);
        }

        @Override
        public Throwable newInstance(final String message, final Throwable cause) throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            return (Throwable) constructor.newInstance(message);
        }
    }

    /**
     * 参数为消息和异常的构造函数
     */
    protected static class ThrowableConstructor2 extends ThrowableConstructor0 {

        public ThrowableConstructor2(Constructor constructor) {
            super(constructor);
        }

        @Override
        public Throwable newInstance(final String message, final Throwable cause) throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            return (Throwable) constructor.newInstance(message, cause);
        }
    }

    /**
     * 参数全部可为空的构造函数
     */
    protected static class ThrowableConstructorNull extends ThrowableConstructor0 {

        protected Object[] params;

        public ThrowableConstructorNull(Constructor constructor) {
            super(constructor);
            params = new Object[constructor.getParameterCount()];
        }

        @Override
        public Throwable newInstance(final String message, final Throwable cause) throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            return (Throwable) constructor.newInstance(params);
        }
    }

}

