package com.zeroone.tenancy.service;

import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.dto.TenancyMetricsQueryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class TenantDataSourceMetricsService {



    private final Map<String, TenancyMetricsDTO> tenancyMetricsDTOMap = new ConcurrentHashMap<>();

    private final Map<String,Set<String>> namedTenancy = new ConcurrentHashMap<>();


    public  TenancyMetricsDTO getTenancyMetrics(String key) {
        return tenancyMetricsDTOMap.get(key);
    }

    public Map<String, Set<String>> getNamedTenancy() {
        return namedTenancy;
    }

    public Set<String> getInstanceId(String serverName){
        return namedTenancy.get(serverName);
    }

    public void cacheTenancyMetrics(TenancyMetricsDTO tenancyMetricsDTO){

        String instanceId = tenancyMetricsDTO.getInstanceId();
        String instanceName = tenancyMetricsDTO.getInstanceName();
        String key = instanceName + "-" + instanceId;

        tenancyMetricsDTOMap.put(key,tenancyMetricsDTO);

        Set<String> named = namedTenancy.computeIfAbsent(instanceName, (k) -> new ConcurrentSkipListSet<>());
        named.add(instanceId);
    }


    public List<TenancyMetricsDTO> queryTenancyMetrics(TenancyMetricsQueryDTO tenancyMetricsQueryDTO){

        return null;
    }




}
