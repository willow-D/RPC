import entity.TestUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import proxy.RpcClientProxy;
import remoting.transport.ClientTransport;
import remoting.transport.netty.client.NettyClientTransport;


@Slf4j
public class NettyClientMain {
    @SneakyThrows
    public static void main(String[] args) {
        ClientTransport rpcClient = new NettyClientTransport();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        EchoService echoService = rpcClientProxy.getProxy(EchoService.class);
        long start = System.currentTimeMillis();
        for(int i=0;i<100000;i++){
            TestUser user = echoService.echo(new TestUser());
            log.info(user.age);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
