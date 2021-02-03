package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.api.TenancyRemoteApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.properties.TenancyClientConfig;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TenancyInitializer implements SmartInitializingSingleton {


    private final Logger log = LoggerFactory.getLogger(getClass());


    private final TenantDataSourceProvider provider;

    private final TenancyRemoteApi tenancyRemoteApi;

    private final TenancyClientConfig tenancyClientConfig;


    public TenancyInitializer(TenantDataSourceProvider provider, TenancyRemoteApi tenancyRemoteApi,TenancyClientConfig tenancyClientConfig) {
        this.provider = provider;
        this.tenancyRemoteApi = tenancyRemoteApi;
        this.tenancyClientConfig = tenancyClientConfig;
    }

    @Override
    public void afterSingletonsInstantiated() {

        Boolean sync = tenancyClientConfig.getSync();

        if (sync == null || sync){
            init();
        }else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    init();
                }finally {
                    executorService.shutdown();
                }
            });
        }
    }

    private void init() {
        //1.获取有效配置信息进行多租户的初始化
        RestResult<List<DataSourceInfo>> configs = tenancyRemoteApi.getAvailableConfigInfo();

        if (!configs.isSuccess()) {
            throw new IllegalStateException("get tenant data source info error [" + configs.getMessage() + "]");
        }
        List<DataSourceInfo> data = configs.getData();

        //2.启动默认执行数据库初始化操作
        provider.prepareDataSourceInfo(data);
    }


    /**
     * 初始化租户数据源
     */
    public boolean initTenantDataSource(String tenantCode) {

        RestResult<DataSourceInfo> dataSourceInfo = tenancyRemoteApi.queryDataSource(tenantCode);

        if (!dataSourceInfo.isSuccess()) {
           log.warn("get tenancy data source info error [{}]",dataSourceInfo.getMessage());
           return false;
        }
        DataSourceInfo data = dataSourceInfo.getData();
        if (data == null){
            return false;
        }
        provider.addDataSource(dataSourceInfo.getData());

        return provider.hasDatasource(tenantCode);
    }
}
