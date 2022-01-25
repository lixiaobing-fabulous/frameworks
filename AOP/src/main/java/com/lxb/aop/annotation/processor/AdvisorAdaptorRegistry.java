package com.lxb.aop.annotation.processor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AdvisorAdaptorRegistry {
    private List<AdvisorAdaptor> adaptors;

    public AdvisorAdaptorRegistry() {
        adaptors = new ArrayList<>();
        adaptors.add(new AroundAdvisorAdaptor());
        adaptors.add(new BeforeAdvisorAdaptor());
        adaptors.add(new AfterReturningAdvisorAdaptor());
        adaptors.add(new AfterThrowingAdvisorAdaptor());
        adaptors.add(new AfterFinallyAdvisorAdaptor());
    }

    public List<AdvisorAdaptor> getAdaptors() {
        return adaptors;
    }

    public void register(AdvisorAdaptor advisorAdaptor) {
        adaptors.add(advisorAdaptor);
    }

    public void deRegister(AdvisorAdaptor advisorAdaptor) {
        adaptors.remove(advisorAdaptor);
    }
}
