package com.remoting.transport;

import com.remoting.packet.RpcRequestPacket;

public interface ClientTransport {

    /*
     * 发送消息到服务端
     * @param rpcRequest 消息体
     * @return 服务端返回的数据
     * */
    Object sendRpcRequest(RpcRequestPacket rpcRequest);
}
