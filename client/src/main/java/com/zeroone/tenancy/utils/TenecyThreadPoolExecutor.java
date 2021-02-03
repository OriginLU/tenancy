package com.zeroone.tenancy.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zeroone.tenancy.thread.pool.ThreadPoolProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.*;

/**
 * 多租户工具类
 */
public class TenecyThreadPoolExecutor {

    private static final Logger log = LoggerFactory.getLogger(TenecyThreadPoolExecutor.class);


    /**
     * 调用者租户信息
     */
    private static final ThreadLocal<String> callerTenant = new ThreadLocal<>();

    /**
     * 多租户工具类执行线程池
     */
    private static final ExecutorService pool = ThreadPoolProxyFactory.createThreadPoolExecutor(
            5,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(2048),
            new ThreadFactoryBuilder().setNameFormat("multi-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 在指定租户下执行方法并返回结果
     *
     * @param executeTenantCode 指定租户
     * @param callable          执行方法
     * @param <T>               返回类型
     * @return 执行结果
     */
    public static <T> T syncExecute(String executeTenantCode, Callable<T> callable) {
        log.debug("execute with return:{},{}", executeTenantCode, callable);
        checkTenantCode(executeTenantCode);
        return execute(executeTenantCode, callable, true);
    }

    /**
     * 在指定租户下执行方法并返回结果
     *
     * @param executeTenantCode 指定租户
     * @param runnable          执行方法
     */
    public static void syncExecute(String executeTenantCode, Runnable runnable) {
        log.debug("execute without return:{},{}", executeTenantCode, runnable);
        checkTenantCode(executeTenantCode);
        execute(executeTenantCode, () -> {
            runnable.run();
            return null;
        }, true);
    }

    /**
     * 校验是否需要跨库执行
     */
    private static void checkTenantCode(final String executeTenantCode) {
        Assert.hasText(executeTenantCode, "租户编码不能为空");
    }

    /**
     * 在指定租户下执行方法无返回结果
     *
     * @param executeTenantCode 指定租户
     * @param runnable          执行方法
     */
    public static void asyncExecute(final String executeTenantCode, final Runnable runnable) {
        log.debug("execute without return:{},{}", executeTenantCode, runnable);
        checkTenantCode(executeTenantCode);
        execute(executeTenantCode, () -> {
            runnable.run();
            return null;
        }, false);
    }

    /**
     * 在指定租户下异步执行方法
     *
     * @param executeTenantCode 执行租户编码
     * @param callable          执行方法
     * @param <T>               返回类型
     * @return 执行结果
     */
    private static <T> T execute(String executeTenantCode, Callable<T> callable, boolean sync) {
        String currentTenantCode = TenantIdentifierHelper.getTenant();
        log.debug("current tenant:{} execute another tenant:{} of {}", currentTenantCode, executeTenantCode, callable);
        try {
            if (sync) {
                // 带返回值执行异步执行
                Future<T> future = pool.submit(() -> execute(currentTenantCode, executeTenantCode, callable));
                return future.get();
            } else {
                pool.execute(() -> execute(currentTenantCode, executeTenantCode, callable));
            }
            return null;
        } catch (Exception e) {
            log.error("async execute failed. tenant code:{},caller:{}", executeTenantCode, getCallerTenant(), e);
            throwUncheck(e);
            return null;
        }
    }


    /**
     * 在默认租户下执行
     *
     * @param callable 执行方法
     * @param <T>      返回类型
     * @return 执行结果
     */
    public static <T> T executeInDefault(final Callable<T> callable) {
        log.debug("execute in default:{}", callable);
        return execute(TenantIdentifierHelper.DEFAULT, callable,true);
    }


    /**
     * 带当前租户执行
     */
    private static <V> V execute(String tenantCode, String executeTenantCode, Callable<V> callable) {
        try {
            //设置执行租户
            TenantIdentifierHelper.setTenant(executeTenantCode);
            setCallerTenant(tenantCode);
            return callable.call();
        } catch (Exception e) {
            throwUncheck(e);
            return null;
        } finally {
            remove();
            TenantIdentifierHelper.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwUncheck(Throwable e) throws E {
        throw (E) e;
    }

    /**
     * 设置租户
     */
    private static void setCallerTenant(String tenantCode) {
        if (null != tenantCode) {
            callerTenant.set(tenantCode);
        }
    }

    /**
     * 获取当前线程对应的调用租户
     */
    public static String getCallerTenant() {
        return callerTenant.get();
    }

    /**
     * 删除线程变量
     */
    private static void remove() {
        callerTenant.remove();
    }
}