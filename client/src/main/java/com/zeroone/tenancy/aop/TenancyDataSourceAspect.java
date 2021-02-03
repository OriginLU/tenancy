package com.zeroone.tenancy.aop;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zeroone.tenancy.annotation.TenancyDataSource;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import com.zeroone.tenancy.thread.pool.ThreadPoolProxyFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 商户数据源切面处理
 */
@Aspect
public class TenancyDataSourceAspect{

    private final Logger log = LoggerFactory.getLogger(TenancyDataSourceAspect.class);

    public static ExecutorService pool = ThreadPoolProxyFactory.createThreadPoolExecutor(
            5,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("task-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());





    private final TenantDataSourceProvider tenantDataSourceProvider;

    public TenancyDataSourceAspect(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    @Pointcut("@annotation(com.zeroone.tenancy.annotation.TenancyDataSource)")
    private void pointcut() {
    }

    /**
     * 切面执行前
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        log.debug("the tenant is {} invoking method {} before. ",
                TenantIdentifierHelper.getTenant(),
                ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
    }

    /**
     * 多租户执行
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Method method = sign.getMethod();
        TenancyDataSource annotation = method.getAnnotation(TenancyDataSource.class);
        Set<String> tenantCodes = new HashSet<>(Arrays.asList(annotation.value()));
        if (CollectionUtils.isEmpty(tenantCodes)) {
            tenantCodes = tenantDataSourceProvider.getDataSourceMap().keySet();
        }
        Object result = null;
        if (annotation.async()) {
            log.info("async execute method:{} cannot get result", method);
            if (null != TenantIdentifierHelper.getTenant()) {
                result = executeMethod(joinPoint, method, TenantIdentifierHelper.getTenant());
            } else {
                for (String tenantCode : tenantCodes) {
                    executeMethodAsync(joinPoint,method, tenantCode);
                }
            }
        } else {
            for (String tenantCode : tenantCodes) {
                result = executeMethod(joinPoint, method, tenantCode);
            }
        }

        if (tenantCodes.size() > 1) {
            log.info("result is the last executed,method:{}, tenant codes:{}", method, tenantCodes);
        }
        return result;
    }

    /**
     * 异步执行方法
     */
    private void executeMethodAsync(ProceedingJoinPoint joinPoint,Method method, String tenantCode) {
        try {
            TenantIdentifierHelper.setTenant(tenantCode);
            pool.execute(() -> ReflectionUtils.invokeMethod(method, joinPoint.getTarget()));
        } catch (Exception e) {
            log.error("invoking method :{},tenant code:{} throwable:{}", method.getName(),tenantCode, e);
        } finally {
            log.debug("the tenant is {} invoking method finally. ", TenantIdentifierHelper.getTenant());
            TenantIdentifierHelper.remove();
        }
    }

    /**
     * 同步执行
     */
    private Object executeMethod(ProceedingJoinPoint joinPoint, Method method, String tenantCode) {
        Object result = null;
        try {
            TenantIdentifierHelper.setTenant(tenantCode);
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 防止某个数据源操作异常导致其他数据源无法操作
            log.error("invoking method :{},tenant code:{} throwable:{}", method.getName(),tenantCode, e);
        } finally {
            log.debug("the tenant is {} invoking method finally. ", TenantIdentifierHelper.getTenant());
            TenantIdentifierHelper.remove();
        }
        return result;
    }


    /**
     * 切面执行后
     */
    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        log.debug("invoking method {} after.",
                ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
        if (null != TenantIdentifierHelper.getTenant()) {
            TenantIdentifierHelper.remove();
        }
    }
}