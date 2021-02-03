package com.zeroone.tenancy.proxy;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

@FeignClient(value = "tenant-client-proxy", configuration = FeignConfig.class)
public interface TenancyClientProxy {


    @PostMapping("add-datasource")
    RestResult<Void> addDatasource(URI url, DataSourceInfo dataSourceInfo);


    /**
     * 移除对应租户数据源
     */
    @GetMapping("remove-datasource/{tenantCode}")
    RestResult<Void> removeDatasource(URI url, @PathVariable("tenantCode") String tenantCode);

}
