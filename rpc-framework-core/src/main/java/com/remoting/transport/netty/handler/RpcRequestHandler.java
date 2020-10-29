package com.remoting.transport.netty.handler;


import com.enumeration.RpcResponseCode;
import com.exception.RpcException;
import com.factory.SingletonFactory;
import com.provider.ServiceProvider;
import com.provider.ServiceProviderImpl;
import com.remoting.packet.RpcRequestPacket;
import com.remoting.packet.RpcResponsePacket;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private static final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public Object handle(RpcRequestPacket rpcRequest){
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
    }


    private Object invokeTargetMethod(RpcRequestPacket rpcRequest, Object service){
        Object result;
        try{
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            if(method==null){
                return RpcResponsePacket.fial(RpcResponseCode.NOT_FOUND_METHOD);
            }
            result = method.invoke(service, rpcRequest.getParameters());
        }catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e){
            throw new RpcException(e.getMessage(),e );
        }
        return result;
    }

}
