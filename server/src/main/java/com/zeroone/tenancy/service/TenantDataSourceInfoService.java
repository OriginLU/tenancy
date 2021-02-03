package com.zeroone.tenancy.service;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.enums.DataBaseTypeEnum;
import com.zeroone.tenancy.enums.DataSourceConfigStatusEnum;
import com.zeroone.tenancy.proxy.TenancyClientProxy;
import com.zeroone.tenancy.repository.TenantDataSourceInfoRepository;
import com.zeroone.tenancy.utils.Reflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
public class TenantDataSourceInfoService implements DisposableBean {


    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String url = "http://{}:{}/";


    private final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3,
            10,
            5,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            new ThreadFactoryBuilder().setNameFormat("tenancy-server-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private TenantDataSourceInfoRepository tenantDataSourceInfoRepository;

    @Autowired
    private TenantDataSourceMetricsService tenantDataSourceMetricsService;

    @Autowired
    private TenancyClientProxy tenancyClientProxy;


    public void saveTenantDataSourceInfo(DataSourceInfo dataSourceInfo) {

        TenantDataSourceInfo tenantDataSourceInfo = new TenantDataSourceInfo();

        BeanUtils.copyProperties(dataSourceInfo, tenantDataSourceInfo);

        tenantDataSourceInfo.setCreateTime(new Date());
        tenantDataSourceInfo.setModifyTime(new Date());

        tenantDataSourceInfoRepository.save(tenantDataSourceInfo);

        poolExecutor.execute(() -> {

            String serverName = dataSourceInfo.getServerName();
            Set<String> instanceIdSet = tenantDataSourceMetricsService.getInstanceId(serverName);
            instanceIdSet.forEach(instanceId -> {

                log.info("notify tenancy client .........");
                String key = serverName + "-" + instanceId;
                TenancyMetricsDTO tenancyMetrics = tenantDataSourceMetricsService.getTenancyMetrics(key);
                String ip = tenancyMetrics.getIp();
                String port = tenancyMetrics.getPort();
                String uri = MessageFormatter.format(url, ip, port).getMessage();


                Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
                        .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                        .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                        .retryIfException()
                        .build();

                try {
                    retryer.call(() -> {
                        try {
                            RestResult<Void> restResult = tenancyClientProxy.addDatasource(new URI(uri), dataSourceInfo);
                            if (!restResult.isSuccess()) {
                                log.error("notify tenancy client instance [{}] error:[{}]", key, restResult.getMessage());
                            }
                        } catch (Exception e) {
                            Reflector.sneakyThrow(e);
                        }
                        return null;
                    });
                } catch (Exception e) {
                    log.error("notify tenancy client instance [" + key + "] error", e);
                }
            });
        });

    }


    public List<DataSourceInfo> getActiveDataSourceInfo(String serverName) {

        List<TenantDataSourceInfo> tenantDataSourceInfos = tenantDataSourceInfoRepository.findByStateAndServerName(DataSourceConfigStatusEnum.ENABLE, serverName);

        if (CollectionUtils.isEmpty(tenantDataSourceInfos)) {
            return Collections.emptyList();
        }
        List<DataSourceInfo> list = new ArrayList<>();
        tenantDataSourceInfos.forEach(tenantDataSourceInfo -> {

            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            BeanUtils.copyProperties(tenantDataSourceInfo, dataSourceInfo);
            list.add(dataSourceInfo);
        });

        return list;
    }

    public DataSourceInfo getSpecifiedActiveDataSourceInfo(String tenantCode, String serverName, DataBaseTypeEnum databaseType) {

        TenantDataSourceInfo tenantDataSourceInfo = tenantDataSourceInfoRepository.findByTenantCodeAndServerNameAndType(tenantCode, serverName, databaseType);

        if (tenantDataSourceInfo == null) {
            return null;
        }
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        BeanUtils.copyProperties(tenantDataSourceInfo, dataSourceInfo);
        return dataSourceInfo;
    }

    @Override
    public void destroy() {

        if (poolExecutor.isShutdown()) {
            poolExecutor.shutdown();
        }
    }
}
