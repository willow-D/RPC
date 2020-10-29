package com.utils.zk;

import com.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CuratorUtils {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 5;
    private static String CONNET_STRING = "";

    static {
        try {
            CONNET_STRING = InetAddress.getLocalHost().getHostAddress()+":2181";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();

    private static CuratorFramework zkClient;

    static {
        zkClient = getZkClient();
    }

    private CuratorUtils(){}

    private static CuratorFramework getZkClient(){
        RetryPolicy retryPolicy  = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(CONNET_STRING)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }

    /*
     * 创建临时化节点
     * @param path 节点路径
     * */
    public static void createEphemeralNode(String path){
        try{
            if(zkClient.checkExists().forPath(path)!=null){
                log.info("节点以存在，节点为:[{}]",path);
            }
            else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                log.info("节点创建成功，节点为：[{}]", path);
            }
        }catch (Exception e){
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

    /*
     * 获取某个节点下的子节点，也就是获取所有提供服务的生产者地址
     * @param serviceName 服务对象接口名
     * @return 指定节点下所有的子节点
     * */
    public static List<String> getChildrenNodes(String serviceName){
        if(serviceAddressMap.containsKey(serviceName)){
            return serviceAddressMap.get(serviceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH+"/"+serviceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, result);
            registerWatcher(zkClient, serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * 清空注册中心的数据
     * */

    public static void clearRegistry(){


    }

    /*
     * 注册监听指定节点
     * @param serviceName 服务对象接口名
     * */
    private static void registerWatcher(CuratorFramework zkClient, String serviceName){
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        }catch (Exception e){
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }
}
