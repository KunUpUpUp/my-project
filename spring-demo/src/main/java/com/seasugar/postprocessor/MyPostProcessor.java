package com.seasugar.postprocessor;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyPostProcessor implements BeanPostProcessor{
    Logger log = org.slf4j.LoggerFactory.getLogger(MyPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("studentServiceImpl".equals(beanName)) {
            log.info("{}初始化之前", beanName);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("studentServiceImpl".equals(beanName)) {
            log.info("{}初始化之后", beanName);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
