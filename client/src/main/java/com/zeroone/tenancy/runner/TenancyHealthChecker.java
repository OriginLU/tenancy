package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.api.TenancyRemoteApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.DatasourceMetrics;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.enums.DatasourceStatusEnum;
import com.zeroone.tenancy.properties.TenancyClientConfig;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 租户心跳信息上送
 */
public class TenancyHealthChecker{

    private final Logger log = LoggerFactory.getLogger(getClass());


    private final TenantDataSourceProvider provider;

    private final TenancyMonitor tenancyMonitor;

    private final TenancyClientConfig tenancyClientConfig;

    private final TenancyRemoteApi tenancyRemoteApi;

    public TenancyHealthChecker(TenantDataSourceProvider provider, TenancyMonitor tenancyMonitor, TenancyClientConfig tenancyClientConfig, TenancyRemoteApi tenancyRemoteApi) {
        this.provider = provider;
        this.tenancyMonitor = tenancyMonitor;
        this.tenancyClientConfig = tenancyClientConfig;
        this.tenancyRemoteApi = tenancyRemoteApi;
    }

    @Scheduled(initialDelay = 10000L,fixedDelay = 30000L)
    public void healthCheck(){


        try {
            Map<String, DatasourceMetrics> metricsMap = tenancyMonitor.getMetricsMap();
            if (CollectionUtils.isEmpty(metricsMap)){
                return;
            }
            //监控数据推送
            pushTenancyMetrics(metricsMap);
            //空闲数据移除策略
            idleCheck(metricsMap);
            //租户数据源检查刷新
            refreshDataSource();
        } catch (Exception e) {
            log.error("health checker happened error",e);
        }

    }

    private void refreshDataSource() {

        RestResult<List<DataSourceInfo>> availableConfigInfo = tenancyRemoteApi.getAvailableConfigInfo();
        if (!availableConfigInfo.isSuccess()) {
            log.warn("get tenant data source info error [{}]",availableConfigInfo.getMessage());
            return;
        }
        List<DataSourceInfo> data = availableConfigInfo.getData();
        data.forEach(dataSourceInfo -> {
            String tenantCode = dataSourceInfo.getTenantCode();

            //是否存在对应的数据源,不存在则进行移除
            if (!provider.hasTenantCode(tenantCode)) {
                provider.remove(tenantCode);
                return;
            }

            if (!provider.hasDatasource(tenantCode)) {
                provider.addDataSource(dataSourceInfo);
            }
        });
    }

    private void idleCheck(Map<String, DatasourceMetrics> metricsMap) {

        metricsMap.forEach((tenantCode, dataMetrics) -> {

            log.info("\n租户:{},\n初始化时间:{},\n创建时间:{},\n重新创建时间:{},\n最近一次使用时间：{},\n使用次数:{},\n运行状态:{}",
                    tenantCode,
                    getTime(dataMetrics.getInitTime()),
                    getTime(dataMetrics.getCreateTime()),
                    getTime(dataMetrics.getRecentlyOverrideTime()),
                    getTime(dataMetrics.getRecentlyUseTime()),
                    dataMetrics.getUseTimes(),
                    DatasourceStatusEnum.fromType(dataMetrics.getStatus()).getDesc());

            //默认数据源不可做删改
            if (dataMetrics.getTenantCode().equals(TenantIdentifierHelper.DEFAULT)) {
                return;
            }
            //执行空闲超时移除逻辑
            if (dataMetrics.getStatus() != DatasourceStatusEnum.RUNNING.getStatus()) {
                return;
            }
            long idleTime = System.currentTimeMillis() - dataMetrics.getRecentlyUseTime();
            Long retrieveTime = tenancyClientConfig.getRetrieveTime();
            if (retrieveTime == null){
                retrieveTime = TenancyClientConfig.DEFAULT_RETRIEVE_TIME;
            }else {
                retrieveTime = TimeUnit.MINUTES.toMillis(retrieveTime);
            }
            if (idleTime >= retrieveTime){
                log.info("idle time is over {} min,remove tenant [{}] datasource", TimeUnit.MILLISECONDS.toMinutes(retrieveTime),tenantCode);
                provider.remove(tenantCode);
            }

        });
    }

    /**
     * 上送数据源监控信息
     */
    private void pushTenancyMetrics(Map<String, DatasourceMetrics> metricsMap) {

        TenancyMetricsDTO tenancyMetricsDTO = new TenancyMetricsDTO();
        tenancyMetricsDTO.setMetricsMap(metricsMap);
        tenancyMetricsDTO.setInstanceId(tenancyClientConfig.getInstanceId());
        tenancyMetricsDTO.setInstanceName(tenancyClientConfig.getInstantName());
        tenancyMetricsDTO.setIp(tenancyClientConfig.getIp());
        tenancyMetricsDTO.setInstanceName(tenancyClientConfig.getInstanceId());

        RestResult<Void> pushResult = tenancyRemoteApi.pushDataSourceMetrics(tenancyMetricsDTO);

        if (!pushResult.isSuccess()) {
            log.warn("push data source metrics info error [{}]",pushResult.getMessage());
        }
    }

    public String getTime(Long time){

        if (time == null){
            return "-1";
        }
        return DateFormatUtils.format(new Date(time),"yyyy-MM-dd HH:mm:ss");
    }
}
