package com;

import com.annotation.RpcServiceScan;
import com.remoting.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.net.UnknownHostException;


@ComponentScan("com")
@RpcServiceScan(basePackage = "com")
public class NettyServerMain {
    public static void main(String[] args) throws UnknownHostException {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();

    }
}
