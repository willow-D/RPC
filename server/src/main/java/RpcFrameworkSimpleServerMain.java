import remoting.transport.socket.SocketRpcServer;

public class RpcFrameworkSimpleServerMain {

    public static void main(String[] args) {
        EchoService echoService = new EchoServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer("192.168.1.104", 8080);
        socketRpcServer.publshService(echoService, EchoService.class);
        socketRpcServer.start();
    }
}
