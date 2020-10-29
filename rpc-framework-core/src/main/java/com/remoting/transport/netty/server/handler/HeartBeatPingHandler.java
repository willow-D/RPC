package com.remoting.transport.netty.server.handler;


import com.remoting.packet.HeartBeatPingPacket;
import com.remoting.packet.HeartBeatPongPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HeartBeatPingHandler extends SimpleChannelInboundHandler<HeartBeatPingPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatPingPacket heartBeatRequest) throws Exception {
        log.info("心跳应答");
        ctx.writeAndFlush(new HeartBeatPongPacket());
    }
}
