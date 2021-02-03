package com.zeroone.tenancy.spring.utils;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public final class ProxyUtils {

    private ProxyUtils() {
        throw new IllegalStateException("Can't instantiate a utility class");
    }


    public static Object getTargetObject(Object candidate) {
        try {
            if (AopUtils.isAopProxy(candidate) && (candidate instanceof Advised)) {
                return  ((Advised) candidate).getTargetSource().getTarget();
            }
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to unwrap proxied object", ex);
        }
        return  candidate;
    }

}
