import remoting.transport.netty.server.NettyServer;

public class NettyServerMain {
    public static void main(String[] args) {
        EchoService echoService = new EchoServiceImpl();
        NettyServer nettyServer = new NettyServer("192.168.1.104", 9999);
        nettyServer.publishService(echoService, EchoService.class);
    }
}
