package proxy;


/*
* 动态代理类。当动态代理对象调用一个方法的时候，实际调用的是下面的 invoke 方法。
* 正是因为动态代理才让客户端调用远程方法像调用本地方法一样简单。
*
* */

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import remoting.dto.RpcMessageChecker;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.transport.ClientTransport;
import remoting.transport.netty.client.NettyClientTransport;
import remoting.transport.socket.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final ClientTransport clientTransport;

    public RpcClientProxy(ClientTransport clientTransport){
        this.clientTransport = clientTransport;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .build();
        RpcResponse rpcResponse = null;
        if(clientTransport instanceof SocketRpcClient){
            rpcResponse = (RpcResponse) clientTransport.sendRpcRequest(rpcRequest);
        }
        if (clientTransport instanceof NettyClientTransport) {
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) clientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }



}
