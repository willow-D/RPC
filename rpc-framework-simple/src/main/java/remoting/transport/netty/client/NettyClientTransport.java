package remoting.transport.netty.client;

import factory.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import registry.ServiceDiscovery;
import registry.ZkServiceDiscovery;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.transport.ClientTransport;

import java.net.InetSocketAddress;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyClientTransport implements ClientTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;  //这个是干嘛的。不是很懂？ 未处理的请求？


    public NettyClientTransport(){
        this.serviceDiscovery = new ZkServiceDiscovery();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

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