package com.lxb.aop.advisor;

import com.lxb.aop.interceptor.MethodInterceptor;
import com.lxb.aop.joinpoint.PointCut;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class DefaultAdvisor implements Advisor {
    private PointCut pointCut;
    private MethodInterceptor methodInterceptor;
    private int priority;


    public DefaultAdvisor(PointCut pointCut, MethodInterceptor methodInterceptor, int priority) {
        this.pointCut = pointCut;
        this.methodInterceptor = methodInterceptor;
        this.priority = priority;
    }

    @Override
    public PointCut getPointCut() {
        return pointCut;
    }

    @Override
    public MethodInterceptor methodInterceptor() {
        return methodInterceptor;
    }

    @Override
    public int priority() {
        return priority;
    }
}
