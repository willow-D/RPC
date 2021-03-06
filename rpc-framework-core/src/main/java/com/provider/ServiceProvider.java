package com.provider;

public interface ServiceProvider {
    /*
     * 保存服务实例对象和服务实例对象实现的接口类的对应关系？
     * @param service      服务实例对象
     * @param serviceClass 服务实例对象实现的接口类
     * @param<T>           服务接口类型
     *
     *
     * */
    void addServiceProvider(Object service, Class<?> serviceClass);

    /*
     * 获取服务实例对象
     * @param serviceName 服务实例对象实现的接口类的类名
     * @return 服务实例对象
     * */

    Object getServiceProvider(String serviceName);
}
