package com.zeroone.tenancy.interceptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeroone.tenancy.constants.TenancyConstants;
import com.zeroone.tenancy.miss.handler.TenantCodeMissHandler;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.runner.TenancyInitializer;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class TenantInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final TenantDataSourceProvider provider;

    private final TenancyInitializer tenancyInitializer;

    private final Set<TenantCodeMissHandler> missHandlers;

    public TenantInterceptor(TenantDataSourceProvider provider, Set<TenantCodeMissHandler> tenantCodeMissHandler,TenancyInitializer tenancyInitializer) {

        this.provider = provider;
        this.missHandlers = tenantCodeMissHandler;
        this.tenancyInitializer = tenancyInitializer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String tenantCode = this.getTenantCode(request);
        log.debug("tenant interceptor set tenant:{}", tenantCode);
        if (StringUtils.isBlank(tenantCode)) {
            log.warn("current tenant code not found : uri -> {}", request.getRequestURI());
            writeErrorResponse(response);
            return false;
        }
        //检查数据源是否存在,不存在则初始化
        if (!provider.hasDatasource(tenantCode) && !tenancyInitializer.initTenantDataSource(tenantCode)) {
            //添加数据源
            writeErrorResponse(response);
            return false;
        }
        //设置租户信息
        TenantIdentifierHelper.setTenant(tenantCode);
        return true;
    }


    private void writeErrorResponse(HttpServletResponse response) throws IOException {

        ServletOutputStream outputStream = response.getOutputStream();

        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("tenant code not found");

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        outputStream.write(objectMapper.writeValueAsBytes(errorResponse));

        outputStream.flush();
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.debug("tenant interceptor remove tenant:{}", TenantIdentifierHelper.getTenant());
        //释放资源
        TenantIdentifierHelper.remove();
    }

    /**
     * 获取租户号
     */
    private String getTenantCode(HttpServletRequest request) {
        String tenantCode = request.getHeader(TenancyConstants.TENANT_CODE);
        if (StringUtils.isBlank(tenantCode) && !CollectionUtils.isEmpty(missHandlers)) {
            return missHandlers.stream().filter(h -> h.match(request)).findFirst().map(h -> h.getTenantCode(request)).orElse(null);
        }
        return tenantCode;
    }
}
