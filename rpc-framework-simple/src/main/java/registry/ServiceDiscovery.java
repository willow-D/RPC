package registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    /*
    * 查找服务
    * @param serviceName 服务名称
    * @return 提供服务
    * */
    InetSocketAddress lookupService(String serviceName);
}
