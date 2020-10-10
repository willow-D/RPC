package remoting.transport.netty.server;

import enumeration.RpcMessageTypeEnum;
import factory.SingletonFactory;
import handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;

@Slf4j
public class NettyServerHandler  extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler(){
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            log.info("server receive msg:[{}]", msg);
            RpcRequest rpcRequest = (RpcRequest) msg;
            if(rpcRequest.getRpcMessageTypeEnum() == RpcMessageTypeEnum.HEART_BEAT){
                log.info("receive heat beat msg from client");
                return;
            }
            Object result = rpcRequestHandler.handle(rpcRequest);
            log.info(String.format("server get result : %s", result.toString()));
            if(ctx.channel().isActive() && ctx.channel().isWritable()){
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
            else{
                log.error("not writable now , message dropped");
            }
        }finally {
            ReferenceCountUtil.release(msg);
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
