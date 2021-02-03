package com.zeroone.tenancy.rest;


import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.dto.TenancyMetricsQueryDTO;
import com.zeroone.tenancy.service.TenantDataSourceMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/data-source")
public class TenantDataSourceResource {


    @Autowired
    private TenantDataSourceMetricsService tenantDataSourceMetricsService;

    /**
     * 缓存数据信息
     */
    @PostMapping("/cache/datasourceMetrics")
    public RestResult<Void> cacheDatasourceMetrics(@RequestBody TenancyMetricsDTO tenancyMetricsDTO){
        tenantDataSourceMetricsService.cacheTenancyMetrics(tenancyMetricsDTO);
        return RestResult.returnSuccess();
    }


    /**
     * 查询监控指标
     */
    @PostMapping("/cache/queryTenancyMetrics")
    public RestResult<List<TenancyMetricsDTO>> cacheDatasourceMetrics(@RequestBody TenancyMetricsQueryDTO tenancyMetricsQueryDTO){
        return RestResult.returnSuccess(tenantDataSourceMetricsService.queryTenancyMetrics(tenancyMetricsQueryDTO));
    }

}
