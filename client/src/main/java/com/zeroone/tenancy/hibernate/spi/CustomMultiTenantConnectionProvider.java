package com.zeroone.tenancy.hibernate.spi;

import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;

/**
 * 加载该类需要通过在配置文件中配置 @see {@link org.hibernate.cfg.AvailableSettings#MULTI_TENANT_CONNECTION_PROVIDER}
 * <pre>
 *     spring:
 *       jpa:
 *        properties:
 *         hibernate.tenant_identifier_resolver: com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider
 * </pre>
 * hibernate tenant spi implementations
 * 数据源选取
 */
public class CustomMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {


    private static final long serialVersionUID = -5005893104168867879L;


    private final TenantDataSourceProvider provider;

    public CustomMultiTenantConnectionProvider(TenantDataSourceProvider tenantDataSourceProvider) {
        this.provider = tenantDataSourceProvider;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource(TenantIdentifierHelper.DEFAULT);
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return provider.getDataSource(tenantIdentifier);
    }
}
