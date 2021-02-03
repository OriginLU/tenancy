package com.zeroone.tenancy.rest;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.enums.DataBaseTypeEnum;
import com.zeroone.tenancy.service.TenantDataSourceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/data-source/config")
public class TenantDataSourceConfigResource  {


    @Autowired
    private TenantDataSourceInfoService tenantDataSourceInfoService;


    @GetMapping("/tenant/server/{serverName}")
    public RestResult<List<DataSourceInfo>> getActiveDataSourceInfo(@PathVariable("serverName") String serverName){
        return RestResult.returnSuccess(tenantDataSourceInfoService.getActiveDataSourceInfo(serverName));
    }

    @PostMapping("save")
    public RestResult<Void> save(@Validated DataSourceInfo dataSourceInfo){

        tenantDataSourceInfoService.saveTenantDataSourceInfo(dataSourceInfo);
        return RestResult.returnSuccess();
    }

    @GetMapping("/tenant/{tenantCode}/server/{serverName}/type/{databaseType}")
    public RestResult<DataSourceInfo> getSpecifiedActiveDataSourceInfo(@PathVariable("tenantCode") String tenantCode, @PathVariable("serverName")String serverName, @PathVariable("databaseType")DataBaseTypeEnum databaseType){
        return RestResult.returnSuccess(tenantDataSourceInfoService.getSpecifiedActiveDataSourceInfo(tenantCode,serverName,databaseType));
    }


}
