package com.lxb.aop.advisor;

import com.lxb.aop.interceptor.MethodInterceptor;
import com.lxb.aop.joinpoint.PointCut;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public interface Advisor {

    PointCut getPointCut();

    MethodInterceptor methodInterceptor();

    int priority();
}
