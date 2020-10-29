package com.remoting.transport.netty.client;

import com.factory.SingletonFactory;
import com.remoting.packet.RpcRequestPacket;
import com.remoting.packet.RpcResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import com.registry.ServiceDiscovery;
import com.registry.ZkServiceDiscovery;
import com.remoting.transport.ClientTransport;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyClientTransport implements ClientTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;


    public NettyClientTransport(){
        this.serviceDiscovery = new ZkServiceDiscovery();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequestPacket rpcRequest) {
        CompletableFuture<RpcResponsePacket> resultFuture = new CompletableFuture<>();

        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress);
        if(channel!=null&&channel.isActive()){
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future->{
                if(future.isSuccess()){
                    log.info("client send message:[{}]", rpcRequest);
                }
                else{
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("send fail : ", future.cause());
                }
            });
        }
        else{
            throw new IllegalStateException();
        }
        return resultFuture;//resultFuture == repsonse
    }
}