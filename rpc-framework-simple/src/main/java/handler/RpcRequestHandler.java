package handler;


import enumeration.RpcResponseCode;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import provider.ServiceProvider;
import provider.ServiceProviderImpl;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private static ServiceProvider  serviceProvider = new ServiceProviderImpl();

    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
    }


    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try{
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            if(method==null){
                return RpcResponse.fial(RpcResponseCode.NOT_FOUND_METHOD);
            }
            result = method.invoke(service, rpcRequest.getParameters());
        }catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e){
            throw new RpcException(e.getMessage(),e );
        }
        return result;
    }

}
