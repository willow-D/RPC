package com.spring;

import com.annotation.RpcServiceScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;


@Component
public class CustomScannerRegistry implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static String RPC_SERVICE_BASE_PACKAGE = "basePackage";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcServiceScan.class.getName()));

        String[] basePackages = rpcScanAnnotationAttributes.getStringArray(RPC_SERVICE_BASE_PACKAGE);
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        CustomScanner customScanner = new CustomScanner(registry);
        if(null!=resourceLoader){
            customScanner.setResourceLoader(resourceLoader);
        }
        customScanner.scan(basePackages);
    }
}
