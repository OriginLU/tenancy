package com.zeroone.tenancy.repository;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.enums.DataBaseTypeEnum;
import com.zeroone.tenancy.enums.DataSourceConfigStatusEnum;
import com.zeroone.tenancy.enums.DatasourceStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantDataSourceInfoRepository extends JpaRepository<TenantDataSourceInfo,Integer> {


    List<TenantDataSourceInfo> findByStateAndTenantCode(DataSourceConfigStatusEnum state, String tenantCode);

    List<TenantDataSourceInfo> findByStateAndServerName(DataSourceConfigStatusEnum state, String serverName);

    TenantDataSourceInfo findByTenantCodeAndServerNameAndType(String tenantCode, String serverName, DataBaseTypeEnum databaseType);
}
