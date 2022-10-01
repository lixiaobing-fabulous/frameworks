package com.lxb.rpc.expression;


import java.util.Map;

/**
 * 表达式接口
 */
public interface Expression {

    /**
     * 表达式计算
     *
     * @param context 上下文
     * @return 计算结果
     */
    Object evaluate(Map<String, Object> context);

}
