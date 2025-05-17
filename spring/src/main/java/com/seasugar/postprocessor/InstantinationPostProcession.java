package com.seasugar.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class InstantinationPostProcession implements InstantiationAwareBeanPostProcessor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InstantinationPostProcession.class);

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if ("studentServiceImpl".equals(beanName)) {
            log.info("{}实例化之前", beanName);
        }
        return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if ("studentServiceImpl".equals(beanName)) {
            log.info("{}实例化之后", beanName);
        }
        return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
    }
}
