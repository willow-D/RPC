package com.remoting.transport.socket;

import com.exception.RpcException;
import com.registry.ServiceDiscovery;
import com.registry.ZkServiceDiscovery;
import com.remoting.packet.RpcRequestPacket;
import com.remoting.transport.ClientTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient(){
        this.serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(RpcRequestPacket rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();

        }catch (IOException | ClassNotFoundException e){
            throw new RpcException("调用服务失败：",e);
        }
    }
}
