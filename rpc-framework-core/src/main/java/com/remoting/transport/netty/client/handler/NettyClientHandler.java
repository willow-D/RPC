package com.remoting.transport.netty.client.handler;

import com.factory.SingletonFactory;
import com.remoting.packet.RpcResponsePacket;
import com.remoting.transport.netty.client.UnprocessedRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;




@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponsePacket> {

    private final UnprocessedRequests unprocessedRequest;

    public NettyClientHandler(){
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponsePacket rpcResponse) throws Exception {
        log.info("client receive msg: [{}]", rpcResponse);
        unprocessedRequest.complete(rpcResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state==IdleState.WRITER_IDLE){
                log.info("write idle happen [{}]",ctx.channel().remoteAddress());

            }
        }
        else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch com.exception:", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
