package com.zeroone.tenancy.utils;

import org.springframework.util.StringUtils;

public class TenantIdentifierHelper {


    public static final String DEFAULT = "default";

    private static final ThreadLocal<String> TENANT_CACHE = new ThreadLocal<>();

    private TenantIdentifierHelper() {
    }

    /**
     * 设置当前线程对应的租户
     */
    public static void setTenant(String tenantCode) {
        if (StringUtils.hasText(tenantCode)) {
            TENANT_CACHE.set(tenantCode);
        }
    }

    /**
     * 获取当前线程对应的租户
     */
    public static String getTenant() {
        return TENANT_CACHE.get();
    }

    /**
     * 删除线程变量
     */
    public static void remove() {
        TENANT_CACHE.remove();
    }
}
