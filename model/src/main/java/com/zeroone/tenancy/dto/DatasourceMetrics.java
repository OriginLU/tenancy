package com.zeroone.tenancy.dto;

/**
 * 数据库使用情况
 */
public class DatasourceMetrics {


    /**
     * 租户号
     */
    private String tenantCode;

    /**
     * 数据库初始化时间
     */
    private Long initTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最近一次使用时间
     */
    private Long recentlyUseTime;

    /**
     * 上次重写时间
     */
    private Long recentlyOverrideTime;

    /**
     * 使用次数
     */
    private int useTimes = 0;

    /**
     * 数据源信息
     */
    private DataSourceInfo dataSourceInfo;

    /**
     * 状态
     */
    private Integer status;

    public Long getRecentlyOverrideTime() {
        return recentlyOverrideTime;
    }

    public void setRecentlyOverrideTime(Long recentlyOverrideTime) {
        this.recentlyOverrideTime = recentlyOverrideTime;
    }



    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getInitTime() {
        return initTime;
    }

    public void setInitTime(Long initTime) {
        this.initTime = initTime;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getRecentlyUseTime() {
        return recentlyUseTime;
    }

    public void setRecentlyUseTime(Long recentlyUseTime) {
        this.recentlyUseTime = recentlyUseTime;
    }

    public Integer getUseTimes() {
        return useTimes;
    }

    public void addUseTimes(){
        this.useTimes ++;
    }



    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }
}
