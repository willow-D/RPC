package com.spring;

import com.annotation.RpcCall;
import com.annotation.RpcService;
import com.factory.SingletonFactory;
import com.provider.ServiceProvider;
import com.provider.ServiceProviderImpl;
import com.proxy.RpcClientProxy;
import com.remoting.transport.ClientTransport;
import com.remoting.transport.netty.client.NettyClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Component
@Slf4j
public class springBeanPostProcessor implements BeanPostProcessor {

    private final ClientTransport rpcClient;
    private final ServiceProvider serviceProvider;

    public springBeanPostProcessor(){
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcClient = new NettyClientTransport();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(RpcService.class)){
            serviceProvider.addServiceProvider(bean, bean.getClass().getInterfaces()[0]);
            log.info("服务注册 : {} ", bean.getClass().getInterfaces()[0]);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for(Field declaredField : declaredFields){
            RpcCall rpcCall = declaredField.getAnnotation(RpcCall.class);
            if(rpcCall!=null){
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
