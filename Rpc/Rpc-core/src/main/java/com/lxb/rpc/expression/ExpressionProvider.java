package com.lxb.rpc.expression;


import com.lxb.extension.Extensible;

/**
 * 表达式引擎
 */
@Extensible("expressionProvider")
public interface ExpressionProvider {

    int MVEL_ORDER = 90;

    int SPEL_ORDER = MVEL_ORDER + 10;

    int JEXL_ORDER = SPEL_ORDER + 10;

    /**
     * 构建表达式
     *
     * @param expression 表达式描述
     * @return 表达式对象
     */
    Expression build(String expression);

}
