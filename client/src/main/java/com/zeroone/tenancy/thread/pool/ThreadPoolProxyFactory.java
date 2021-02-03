package com.zeroone.tenancy.thread.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Queue;
import java.util.concurrent.*;

/**
 *
 */
public final class ThreadPoolProxyFactory {

    private static final Queue<Executor> executorList = new ConcurrentLinkedDeque<>();

    private ThreadPoolProxyFactory() {
    }

    public static void shutdown(ExecutorServiceProxy executorServiceProxy) {
        executorServiceProxy.shutdown();
        executorList.remove(executorServiceProxy);
    }

    public static synchronized void shutdownAll() {
        Executor executor;
        do {
            executor = executorList.poll();
            if (executor != null) {
                if (executor instanceof DisposableBean) {
                    DisposableBean bean = (DisposableBean) executor;
                    try {
                        bean.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (executor instanceof ExecutorService) {
                    ExecutorService executorService = (ExecutorService) executor;
                    executorService.shutdown();
                }
            }
        } while (executor != null);
    }


    public static ExecutorService createThreadPoolExecutor() {
        return createThreadPoolExecutor(
            5,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static ExecutorService createThreadPoolExecutor(int corePoolSize,
                                                           int maximumPoolSize,
                                                           long keepAliveTime,
                                                           TimeUnit unit,
                                                           BlockingQueue<Runnable> workQueue,
                                                           ThreadFactory threadFactory,
                                                           RejectedExecutionHandler handler) {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory,
            handler
        );
        ExecutorService result = new ExecutorServiceProxy(executorService);
        executorList.add(result);
        return result;
    }

    public static AsyncTaskExecutor createAsyncTaskExecutor(int corePoolSize,
                                                            int maxPoolSize,
                                                            int queueCapacity,
                                                            String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        AsyncTaskExecutor result = new AsyncTaskExecutorProxy(executor);
        executorList.add(result);
        return result;
    }
}
