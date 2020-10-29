package com;

import com.entity.TestUser;
import com.proxy.RpcClientProxy;
import com.remoting.transport.ClientTransport;
import com.remoting.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketClientMain {
    public static void main(String[] args){
        ClientTransport clientTransport = new SocketRpcClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport);
        EchoService echoService = rpcClientProxy.getProxy(EchoService.class);
        long startTime = System.currentTimeMillis();
        for(int i=0;i<10000;i++){
            TestUser user = echoService.echo(new TestUser());
            log.info(user.age);
        }
        System.out.println(System.currentTimeMillis() - startTime);

    }
}

