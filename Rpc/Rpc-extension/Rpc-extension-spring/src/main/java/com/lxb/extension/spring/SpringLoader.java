package com.lxb.extension.spring;

import com.lxb.extension.ExtensionLoader;
import com.lxb.extension.ExtensionManager;
import com.lxb.extension.Instantiation;
import com.lxb.extension.Name;
import com.lxb.extension.Plugin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Spring加载器
 */
public class SpringLoader implements ExtensionLoader, PriorityOrdered, ApplicationContextAware,
        BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextClosedEvent> {

    protected ApplicationContext context;

    protected Instantiation instance;

    protected BeanDefinitionRegistry registry;

    @Override
    public <T> Collection<Plugin<T>> load(final Class<T> extensible) {
        if (extensible == null) {
            return null;
        }
        List<Plugin<T>> result = new LinkedList<>();
        if (registry != null) {
            BeanDefinition definition;
            Class<?>       clazz;
            for (String name : registry.getBeanDefinitionNames()) {
                definition = registry.getBeanDefinition(name);
                if (!definition.isAbstract()) {
                    clazz = getBeanClass(definition);
                    if (clazz != null && extensible.isAssignableFrom(clazz)) {
                        //延迟加载，防止Bean还没有初始化好
                        result.add(new Plugin<T>(new Name<>((Class<T>) clazz, name), instance,
                                definition.isSingleton(), null, this));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取Bean的类
     *
     * @param definition 定义
     * @return Bean的类
     */
    protected Class<?> getBeanClass(BeanDefinition definition) {
        if (definition == null) {
            return null;
        }
        //判断spring是否已经获得了Class
        Class<?> clazz = null;
        if (definition instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition abd = (AbstractBeanDefinition) definition;
            //动态创建的bean，beanDefinition上的getBeanClass返回的是创建该bean的类
            if (abd.hasBeanClass() && isEmpty(definition.getFactoryMethodName())) {
                return abd.getBeanClass();
            }
        }
        //拿到定义
        if (isEmpty(definition.getBeanClassName())) {
            if (!isEmpty(definition.getFactoryBeanName())) {
                clazz = getBeanClass(registry.getBeanDefinition(definition.getFactoryBeanName()));
            }
        } else {
            try {
                clazz = ClassUtils.forName(definition.getBeanClassName(), Thread.currentThread().getContextClassLoader());
            } catch (Exception ignored) {
            }
        }
        if (clazz != null && !isEmpty(definition.getFactoryMethodName())) {
            //找到方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(definition.getFactoryMethodName())) {
                    //获取方法的返回类型
                    return method.getReturnType();
                }
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
        this.instance = new Instantiation() {
            @Override
            public <T, M> T newInstance(final Name<T, M> name) {
                try {
                    return context.getBean(name.getName().toString(), name.getClazz());
                } catch (BeansException e) {
                    return null;
                }
            }
        };
        //注册当前插件加载器
        ExtensionManager.register(this);
    }

    @Override
    public void onApplicationEvent(final ContextClosedEvent event) {
        //容器停止的时候，注销当前插件加载器
        if (event.getSource() == context) {
            ExtensionManager.deregister(this);
        }
    }
}