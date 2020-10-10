package remoting.transport.netty.client;


import factory.SingletonFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class  ChannelProvider {

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();
    private static NettyClient nettyClient;

    static {
        nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }
    private ChannelProvider(){

    }

    public static Channel get(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channel!=null&&channel.isActive()){
                return channel;
            }
            else{
                channels.remove(key);
            }
        }
        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channels.put(key, channel);
        return channel;
    }

    public static void remove(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        channels.remove(key);
        log.info("channel map size : [{}]", channels.size());
    }
}
