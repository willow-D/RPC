import entity.TestUser;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;
import proxy.RpcClientProxy;
import remoting.transport.ClientTransport;
import remoting.transport.socket.SocketRpcClient;
@Slf4j
public class RpcFrameworkSimpleClientMain {
    public static void main(String[] args){
        ClientTransport clientTransport = new SocketRpcClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport);
        EchoService echoService = rpcClientProxy.getProxy(EchoService.class);
        long startTime = System.currentTimeMillis();
        for(int i=0;i<100000;i++){
            TestUser user = echoService.echo(new TestUser());
            log.info(user.age);
        }
        System.out.println(System.currentTimeMillis() - startTime);

    }
}

