package com.remoting.transport.netty.server.handler;

import com.factory.SingletonFactory;
import com.remoting.packet.RpcRequestPacket;
import com.remoting.packet.RpcResponsePacket;
import com.remoting.transport.netty.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequestPacket> {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler(){
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestPacket rpcRequest) throws Exception {
        Object result = rpcRequestHandler.handle(rpcRequest);
        log.info(String.format("server get result : %s", result.toString()));
        if(ctx.channel().isActive() && ctx.channel().isWritable()){
            RpcResponsePacket<Object> rpcResponse = RpcResponsePacket.success(result, rpcRequest.getRequestId());
            ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        else{
            log.error("not writable now , message dropped");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        }
        else{
            super.userEventTriggered(ctx, evt);
        }
    }
}
