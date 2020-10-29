package com.proxy;


/*
* 动态代理类。当动态代理对象调用一个方法的时候，实际调用的是下面的 invoke 方法。
* 正是因为动态代理才让客户端调用远程方法像调用本地方法一样简单。
*
* */


import com.remoting.packet.RpcRequestPacket;
import com.remoting.packet.RpcResponsePacket;
import com.remoting.transport.ClientTransport;
import com.remoting.transport.netty.client.NettyClientTransport;
import com.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
        RpcRequestPacket rpcRequest = RpcRequestPacket.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .build();
        RpcResponsePacket rpcResponse = null;

        if (clientTransport instanceof NettyClientTransport) {
            CompletableFuture<RpcResponsePacket> completableFuture = (CompletableFuture<RpcResponsePacket>) clientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        if(clientTransport instanceof SocketRpcClient){
            rpcResponse = (RpcResponsePacket) clientTransport.sendRpcRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }

}
