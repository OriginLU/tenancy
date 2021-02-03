package com.zeroone.tenancy.constants;

public interface TenancyApiConstants {

    interface Query {
        /**
         * 查询全部数据源
         */
        String QUERY_ALL_DATA_SOURCE = "/api/data-source/config/tenant/server/%s";
        /**
         * 查询某个租户数据源
         */
        String QUERY_TENANT_DATA_SOURCE = "/api/data-source/config/tenant/%s/server/%s/type/%s";

        /**
         * 租户数据源状态
         */
        String TENANT_DATA_SOURCE_STATUS = "/api/data-source/config/server/%s/status";

        /**
         * 上报数据监控信息
         */
        String PUSH_DATA_SOURCE_METRICS = "/api/tenant/data-source/cache/datasourceMetrics";
    }

    interface TenancyMethods {

        /**
         * 创建租户映射
         */
        String CREATE_TENANT_BIZ_MAPPED = "/api/tenant-business-records";

        /**
         * 查询租户映射
         */
        String QUERY_TENANT_BIZ_MAPPED = "/api/tenant-business-records/business/%s/code/%s";


        /**
         * id生成
         */
        String ID_GENERATE = "/api/id-generate";




    }
}
