package com.spring;

import com.annotation.RpcService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class CustomScanner extends ClassPathBeanDefinitionScanner {
    public CustomScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        super.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
    }
}
