package com.zeroone.tenancy.interceptor;


import com.zeroone.tenancy.constants.TenancyConstants;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 服务透传拦截器
 */
public class HeaderInterceptor implements RequestInterceptor {



    /**
     * 服务间需要透传的头
     */
    private final Set<String> HEADER_NAMES = new HashSet<>(Arrays.asList("tenant_code", "oauth2-authentication", "oauth2-authority"));

    public HeaderInterceptor(String ...args) {
        HEADER_NAMES.addAll(Arrays.asList(args));
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        //透传租户信息
        requestTemplate.header(TenancyConstants.TENANT_CODE, TenantIdentifierHelper.getTenant());

        // 微服务透传header
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (null == attributes) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (HEADER_NAMES.contains(headerName)) {
                    String value = request.getHeader(headerName);
                    requestTemplate.header(headerName, value);
                }
            }
        }

    }
}