package registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    /*
    * 注册服务
    * @param serviceName
    * @param inetSocketAddress
    * */

    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
}
