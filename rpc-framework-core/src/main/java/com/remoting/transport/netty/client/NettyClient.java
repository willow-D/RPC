package com.remoting.transport.netty.client;



import com.remoting.transport.netty.client.handler.HeartBeatTimerHandler;
import com.remoting.transport.netty.client.handler.NettyClientHandler;
import com.remoting.transport.netty.handler.PacketDecoder;
import com.remoting.transport.netty.handler.PacketEncoder;
import com.remoting.transport.netty.handler.Spliter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.serialize.kyro.KryoSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
    private static Bootstrap bootstrap;
    private static EventLoopGroup eventLoopGroup;

    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0,0,2, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new HeartBeatTimerHandler());
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });

    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completeFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
            if(future.isSuccess()){
                log.info("客户端连接成功");
                completeFuture.complete(future.channel());
            }
            else{
                throw new IllegalStateException();
            }
        });
        return completeFuture.get();
    }

    public void close(){
        log.info("call close method");
        eventLoopGroup.shutdownGracefully();
    }
}
