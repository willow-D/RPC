package com.utils.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolFactoryUtils {
    /*
     *
     * 通过 threadNamePrefix 来区分不同线程池（我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key:threadNamePrefix
     * value:threadPool
     */

    private static Map<String, ExecutorService> threadPools = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtils(){}

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);

    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix){
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        //先检测是否存在该线程池，不存在就创建，存在就检测该线程池状态，如果已经关闭了，就删掉，重新创一个。
        ExecutorService threadPool = threadPools.computeIfAbsent(threadNamePrefix, k->createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));

        if(threadPool.isShutdown()||threadPool.isTerminated()){
            threadPools.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            threadPools.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean deamon){
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, deamon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaxPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(),customThreadPoolConfig.getUnit(),customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    /*
     * 创建ThreadFactory,如果threadNamePrefix不为空则使用自建ThreadFactory,否则使用defaultThreadFactory
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon 是否指定为守护线程
     * @return ThreadFactory
     * */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){
        if(threadNamePrefix!=null){
            if(daemon!=null){
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            }
            else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAllThreadPool(){
        log.info("call shutDownAllThreadPool method");
        threadPools.entrySet().parallelStream().forEach(entry->{
            entry.getValue().shutdown();
            log.info("shut down thread pool [{}][{}]", entry.getKey(),entry.getValue().isTerminated());
            try {
                entry.getValue().awaitTermination(10, TimeUnit.SECONDS);
            }catch (InterruptedException e){
                log.error("Thred pool never terminated");
                entry.getValue().shutdownNow();
            }
        });
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status",false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}

