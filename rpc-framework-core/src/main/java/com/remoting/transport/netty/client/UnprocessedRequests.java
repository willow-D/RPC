package com.remoting.transport.netty.client;

import com.remoting.packet.RpcResponsePacket;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {

    public UnprocessedRequests(){};

    private static Map<String, CompletableFuture<RpcResponsePacket>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponsePacket> future){
        unprocessedResponseFutures.put(requestId, future);
    }

    public void complete(RpcResponsePacket rpcResponse){
        CompletableFuture<RpcResponsePacket> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if(future!=null){
            future.complete(rpcResponse); //将rpcResponse 赋值给future
        }else{
            throw  new IllegalStateException();
        }
    }

}
