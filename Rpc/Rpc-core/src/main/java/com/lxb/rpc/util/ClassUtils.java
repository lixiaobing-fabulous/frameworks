package com.lxb.rpc.util;

import com.lxb.rpc.exception.CreationException;
import com.lxb.rpc.exception.MethodOverloadException;
import com.lxb.rpc.exception.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ClassUtils {
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
     * 迭代父类
     *
     * @param clazz 类
     * @return 父类迭代器
     */
    public static Iterator<Class<?>> iterate(final Class<?> clazz) {
        return new SuperIterator(clazz);
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
     * 类元数据
     */
    protected static class ClassMeta {
        /**
         * 类型
         */
        protected Class<?> type;

    }

    /**
     * 字段元数据
     */
    protected static class FieldMeta {
        /**
         * 类型
         */
        protected Class<?>           type;
        /**
         * 字段
         */
        protected List<Field>        fields = new LinkedList<>();
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
        protected Class<?>                    type;
        /**
         * 公共重载信息
         */
        protected Map<String, OverloadMethod> overloadMethods;
        /**
         * 读
         */
        protected Map<String, Method>         getter;
        /**
         * 写方法
         */
        protected Map<String, Method>         setter;

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
        protected Class<?>                 clazz;
        /**
         * 名称
         */
        protected String                   name;
        /**
         * 第一个方法
         */
        protected MethodInfo               first;
        /**
         * 多个方法的签名
         */
        protected Map<Integer, MethodInfo> signs;
        /**
         * 方法元数据
         */
        protected Map<Method, MethodInfo>  metas;

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
        protected          Class<?>      clazz;
        /**
         * 方法
         */
        protected          Method        method;
        /**
         * 名称
         */
        protected          String        name;
        /**
         * 签名
         */
        protected          int           sign;
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
        protected Class<?>                      type;
        /**
         * 单参数公开的构造函数
         */
        protected Map<Class<?>, Constructor<?>> singleConstructors = new HashMap<>(3);
        /**
         * 默认公开的构造函数
         */
        protected Constructor<?>                defaultConstructor;
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
}
