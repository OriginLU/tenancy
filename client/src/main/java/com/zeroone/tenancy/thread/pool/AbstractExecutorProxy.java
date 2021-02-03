package com.zeroone.tenancy.thread.pool;

import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 *
 */
public abstract class AbstractExecutorProxy implements Executor, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(AbstractExecutorProxy.class);

    protected abstract Executor getExecutor();

    @Override
    public void execute(Runnable task) {
        this.getExecutor().execute(this.createWrappedRunnable(task));
    }

    protected <T> Callable<T> createCallable(final Callable<T> task) {
        final String currentTenant = TenantIdentifierHelper.getTenant();
//        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            try {
                TenantIdentifierHelper.setTenant(currentTenant);
//                ThreadContextUtil.setContext(securityContext);
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            } finally {
                TenantIdentifierHelper.remove();
//                ThreadContextUtil.clearContext();
            }
        };
    }

    protected Runnable createWrappedRunnable(final Runnable task) {
        final String currentTenant = TenantIdentifierHelper.getTenant();
//        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            try {
                TenantIdentifierHelper.setTenant(currentTenant);
//                ThreadContextUtil.setContext(securityContext);
                task.run();
            } catch (Exception e) {
                handle(e);
                throw e;
            } finally {
                TenantIdentifierHelper.remove();
//                ThreadContextUtil.clearContext();
            }
        };
    }


    protected void handle(Exception e) {
        log.error("caught exception", e);
    }
}
