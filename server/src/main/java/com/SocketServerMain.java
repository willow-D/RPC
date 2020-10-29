package com;

import com.remoting.transport.socket.SocketRpcServer;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SocketServerMain {

    @SneakyThrows
    public static void main(String[] args) {
        EchoService echoService = new EchoServiceImpl();
        String host = InetAddress.getLocalHost().getHostAddress();
        SocketRpcServer socketRpcServer = new SocketRpcServer(host, 8080);
        socketRpcServer.publshService(echoService, EchoService.class);
        socketRpcServer.start();
    }
}
