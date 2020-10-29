package com.remoting.transport.socket;



import com.provider.ServiceProvider;
import com.provider.ServiceProviderImpl;
import com.utils.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {
    private final ExecutorService threadPool;
    private final String host;
    private final int port;

    private final ServiceProvider serviceProvider;

    public SocketRpcServer(String host, int port){
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");

        serviceProvider = new ServiceProviderImpl();
    }

    public <T> void publshService(T service, Class<T> serviceClass){
        serviceProvider.addServiceProvider(service, serviceClass);

    }

    public void start(){
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, port));
            Socket socket;
            while ((socket = server.accept()) != null) {  //?
                log.info("client connected [{}] ", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        }catch (IOException e){
            log.error("occur IOException", e);
        }
    }
}
