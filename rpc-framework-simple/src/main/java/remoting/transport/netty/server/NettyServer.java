package remoting.transport.netty.server;

import config.CustomShutdownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import provider.ServiceProvider;
import provider.ServiceProviderImpl;
import registry.ServiceRegistry;
import registry.ZkServiceRegistry;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.transport.netty.codec.kryo.NettyKryoDecoder;
import remoting.transport.netty.codec.kryo.NettyKryoEncoder;
import serialize.kyro.KryoSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {
    private String host;
    private int port;
    private KryoSerializer kryoSerializer;
    private ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;


    public NettyServer(String host, int port){
        this.host = host;
        this.port = port;
        kryoSerializer = new KryoSerializer();
        serviceRegistry = new ZkServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }

    public <T> void publishService(T service, Class<T> serviceClass){
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    private void start(){
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                           // ch.pipeline().addLast(new IdleStateHandler(30, 30,0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture f = b.bind(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
