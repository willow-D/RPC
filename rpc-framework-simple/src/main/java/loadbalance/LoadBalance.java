package loadbalance;

import java.util.List;

public interface LoadBalance {
    /*
    * 在已有的服务提供地址列表中选择一个
    * @param serviceAddress 服务地址列表
    * @return 目标服务地址
    *
    * */

    String selectServiceAddress(List<String> serviceAddress);
}
