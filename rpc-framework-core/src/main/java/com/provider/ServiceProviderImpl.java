package com.provider;

import com.enumeration.RpcErrorMessageEnum;
import com.exception.RpcException;
import com.registry.ServiceRegistry;
import com.registry.ZkServiceRegistry;
import com.remoting.transport.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private final ServiceRegistry serviceRegistry = new ZkServiceRegistry();
    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static Set<String> registeredService = ConcurrentHashMap.newKeySet();



    @Override
    public  void addServiceProvider(Object service, Class<?> serviceClass) {
        String serviceName = serviceClass.getName();
        if(registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(NettyServer.host, NettyServer.PORT));
        log.info("Add service: {} and interfaces : {} ", serviceName, service.getClass());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service==null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
