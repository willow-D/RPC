package com.remoting.transport.netty.server;


import com.remoting.transport.netty.handler.PacketDecoder;
import com.remoting.transport.netty.handler.PacketEncoder;
import com.remoting.transport.netty.handler.Spliter;
import com.remoting.transport.netty.server.handler.HeartBeatPingHandler;
import com.remoting.transport.netty.server.handler.NettyServerHandler;
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
import com.serialize.kyro.KryoSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {
    public static int PORT = 8080;
    public static String host;

    static {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private KryoSerializer kryoSerializer;

    public NettyServer(){
        kryoSerializer = new KryoSerializer();
    }

    public void start(){

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
                            ch.pipeline().addLast(new IdleStateHandler(0, 0,2, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new Spliter());
                            ch.pipeline().addLast(new PacketDecoder());
                            ch.pipeline().addLast(new PacketEncoder());
                            ch.pipeline().addLast(new HeartBeatPingHandler());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture f = b.bind(host, PORT).sync();
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
