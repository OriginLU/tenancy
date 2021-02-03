package com.zeroone.tenancy.annotation;

import com.zeroone.tenancy.autoconfigure.TenancyAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TenancyAutoConfiguration.class)
public @interface EnableTenancyClient {

}
