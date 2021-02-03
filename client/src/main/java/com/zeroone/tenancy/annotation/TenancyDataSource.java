package com.zeroone.tenancy.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenancyDataSource {
    /**
     * 租户名称
     * 默认为空表示所有数据源都会遍历访问
     */
    String[] value() default {};

    /**
     * 默认异步执行
     */
    boolean async() default true;
}