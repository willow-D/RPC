package com.registry;

import com.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        CuratorUtils.createEphemeralNode(servicePath);
    }
}
