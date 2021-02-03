package com.zeroone.tenancy.dto;


import java.util.Map;

public class TenancyMetricsDTO {

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 实例Ip
     */
    private String ip;

    /**
     * 实例端口
     */
    private String port;

    /**
     * 监控参数
     */
    private Map<String, DatasourceMetrics> metricsMap;


    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<String, DatasourceMetrics> getMetricsMap() {
        return metricsMap;
    }

    public void setMetricsMap(Map<String, DatasourceMetrics> metricsMap) {
        this.metricsMap = metricsMap;
    }
}
