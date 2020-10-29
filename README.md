# RPC

基于 Netty+Kyro+Zookeeper 实现的 RPC 框架

### 版本一：基于Socket+Zookeeper+动态代理实现自定义RPC同步调用框架
- 采用基于BIO的Socket实现网络传输，并与线程池配合使用，提高服务端的并发性能。
- 使用Zookeeper作为注册中心，实现服务的注册与发现功能。
- 运用反射技术，实现动态代理，从而屏蔽远程调用的具体细节。
- 实现负载均衡功能
### 版本二：基于Netty+Zookeeper+Kryo实现自定义RPC异步调用框架
- 采用基于NIO的Netty替代Socket，进一步的提升并发性能。
- 使用CompletableFuture优化接受服务端返回结果，从而实现服务的异步调用。
- 使用开源的序列化机制Kryo替代 JDK 自带的序列化机制，提高序列化性能。


### 涉及知识点
- Socket编程
- Netty
- Zookeeper 以及 Curator使用
- 序列化机制
- 异步编程与CompletableFuture
- 反射与动态代理
- 自定义注解
- 自定义包扫描
- springBeanPostProcessor
