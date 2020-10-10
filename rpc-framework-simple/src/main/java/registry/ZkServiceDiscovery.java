package registry;

import loadbalance.LoadBalance;
import loadbalance.RandomLoadBalance;
import lombok.extern.slf4j.Slf4j;
import utils.zk.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery{

    private final LoadBalance loadBalance;


    public ZkServiceDiscovery(){
        this.loadBalance = new RandomLoadBalance();
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> serviceURLList = CuratorUtils.getChildrenNodes(serviceName);
        String targetServiceURL = loadBalance.selectServiceAddress(serviceURLList);
        log.info("成功找到服务地址：[{}]",targetServiceURL);
        String[] socketAddressArray = targetServiceURL.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);

        return new InetSocketAddress(host, port);
    }
}
