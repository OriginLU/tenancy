package com.zeroone.tenancy.resource;

import com.zeroone.tenancy.annotation.TenancyApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@TenancyApi
@RestController
@RequestMapping("/tenancy")
public class TenancyResource extends BaseExceptionHandler{

    @Autowired
    private TenantDataSourceProvider provider;


    /**
     * 添加租户数据源
     */
    @PostMapping("add-datasource")
    public RestResult<Void> addDatasource(DataSourceInfo dataSourceInfo){
        provider.addDataSource(dataSourceInfo);
        return RestResult.returnSuccess();
    }


    /**
     * 移除对应租户数据源
     */
    @GetMapping("remove-datasource/{tenantCode}")
    public RestResult<Void> removeDatasource(@PathVariable("tenantCode") String tenantCode){
        provider.remove(tenantCode);
        return RestResult.returnSuccess();
    }
}
