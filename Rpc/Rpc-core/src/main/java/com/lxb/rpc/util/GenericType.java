package com.lxb.rpc.util;


import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;

/**
 * 代表参数、返回值和异常的泛型信息
 */
public class GenericType {
    /**
     * 字段或参数的类型
     */
    protected Type genericType;
    /**
     * 类
     */
    protected Class<?> type;
    /**
     * 单一变量
     */
    protected Variable variable;
    /**
     * 变量集合
     */
    protected Map<String, Variable> variables;

    /**
     * 构造函数
     *
     * @param genericType 泛型类型
     * @param type        类
     */
    public GenericType(final Type genericType, final Class<?> type) {
        this.genericType = genericType;
        this.type = type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * 用于更新识别的泛型
     *
     * @param genericType 识别的泛型
     */
    protected void setGenericType(Type genericType) {
        this.genericType = genericType;
        if (type != genericType) {
            if (genericType instanceof Class) {
                this.type = (Class) genericType;
            } else if (genericType instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) genericType).getRawType();
                if (rawType instanceof Class && rawType != type) {
                    type = (Class) rawType;
                }
            }
        }
    }

    /**
     * 添加变量
     *
     * @param variable
     */
    protected void addVariable(final Variable variable) {
        if (variable == null) {
            return;
        }
        if (this.variable == null) {
            this.variable = variable;
        } else {
            if (variables == null) {
                variables = new HashMap<>();
                variables.put(this.variable.name, this.variable);
            }
            variables.put(variable.name, variable);
        }
    }

    /**
     * 获取变量
     *
     * @param name
     * @return
     */
    public Variable getVariable(final String name) {
        if (name == null) {
            return null;
        }
        if (variables == null) {
            return variable != null && variable.name.equals(name) ? variable : null;
        } else {
            return variables.get(name);
        }
    }

    /**
     * 获取泛型变量
     *
     * @return 泛型变量
     */
    public Map<String, Type> getVariables() {
        Map<String, Type> result = null;
        if (variables == null) {
            if (variable == null) {
                return null;
            }
            result = new HashMap<>();
            result.put(variable.getName(), variable.getGenericType());
        } else {
            result = new HashMap<>(variables.size());
            for (Map.Entry<String, Variable> entry : variables.entrySet()) {
                result.put(entry.getKey(), entry.getValue().getGenericType());
            }
        }
        return result;
    }

    /**
     * 验证类型，防止漏洞攻击
     *
     * @param type   目标类型
     * @param clazz  调用方指定的类
     * @param parent 判断目标类型可以被调用方指定的类赋值
     * @return 是否有效
     */
    public static boolean validate(final Type type, final Class<?> clazz, final boolean parent) {
        if (type instanceof Class) {
            //防止漏洞攻击
            return parent ? ((Class) type).isAssignableFrom(clazz) : clazz.isAssignableFrom((Class) type);
        } else if (type instanceof GenericArrayType) {
            //验证数组
            return clazz.isArray() && validate(((GenericArrayType) type).getGenericComponentType(), clazz.getComponentType(), parent);
        } else if (type instanceof ParameterizedType) {
            return validate(((ParameterizedType) type).getRawType(), clazz, parent);
        } else if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable) type).getBounds();
            if (parent && bounds != null) {
                for (Type t : bounds)
                    if (!validate(t, clazz, true)) {
                        return false;
                    }
            }
            return true;
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
            if (parent && upperBounds != null) {
                for (Type t : upperBounds)
                    if (!validate(t, clazz, true)) {
                        return false;
                    }
            }
            if (parent && lowerBounds != null) {
                for (Type t : lowerBounds)
                    if (!validate(t, clazz, false)) {
                        return false;
                    }
            }
            return true;
        }
        return false;
    }

    /**
     * 计算泛型参数位置
     *
     * @param parameters
     */
    protected void compute(final Map<String, Integer> parameters) {
        if (variables != null) {
            variables.values().forEach(v -> compute(parameters, v));
        } else if (variable != null) {
            compute(parameters, variable);
        }
    }

    /**
     * 计算泛型参数位置
     *
     * @param parameters
     * @param variable
     */
    protected void compute(final Map<String, Integer> parameters, final Variable variable) {
        Integer pos = parameters.get(variable.name);
        if (pos != null) {
            variable.parameter = pos;
        }
    }

    /**
     * 泛型变量
     */
    public static class Variable {
        /**
         * 名称
         */
        protected String name;
        /**
         * 类型，可以是Class、ParameterizedType、GenericArrayType和Wi
         */
        protected Type genericType;
        /**
         * 第几个参数代表类型
         */
        protected int parameter = -1;

        public Variable(String name) {
            this.name = name;
        }

        public Variable(String name, Type genericType) {
            this.name = name;
            this.genericType = genericType;
        }

        public Variable(String name, Variable variable) {
            this.name = name;
            if (variable != null) {
                this.genericType = variable.genericType;
            }
        }

        public String getName() {
            return name;
        }

        public Type getGenericType() {
            return genericType;
        }

        public int getParameter() {
            return parameter;
        }

    }
}
