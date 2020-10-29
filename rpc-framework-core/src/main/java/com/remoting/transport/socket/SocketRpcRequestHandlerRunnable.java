package com.remoting.transport.socket;

import com.factory.SingletonFactory;

import com.remoting.packet.RpcRequestPacket;
import com.remoting.packet.RpcResponsePacket;
import com.remoting.transport.netty.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRpcRequestHandlerRunnable  implements Runnable{

    private Socket socket;
    private RpcRequestHandler rpcRequestHandler;

    public SocketRpcRequestHandlerRunnable(Socket socket){
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);

    }

    @Override
    public void run() {
        log.info("server handle message from client by thread : [{}]", Thread.currentThread().getName());
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequestPacket rpcRequest = (RpcRequestPacket) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponsePacket.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException e){
            log.error("occur com.exception:", e);
        }

    }
}
