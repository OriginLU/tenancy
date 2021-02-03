package com.zeroone.tenancy.mybatis.datasource;

import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class RoutingDataSource extends AbstractDataSource {


    private final TenantDataSourceProvider tenantDataSourceProvider;


    public RoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    protected DataSource determineTargetDataSource() {
        return tenantDataSourceProvider.getDataSource(determineCurrentLookupKey());
    }

    protected String determineCurrentLookupKey() {
        return Optional.ofNullable(TenantIdentifierHelper.getTenant()).orElse(TenantIdentifierHelper.DEFAULT);
    }
}
