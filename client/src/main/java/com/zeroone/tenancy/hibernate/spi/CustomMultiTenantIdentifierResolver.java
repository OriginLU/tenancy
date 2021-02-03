package com.zeroone.tenancy.hibernate.spi;

import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Optional;

/**
 * 加载该类需要通过在配置文件中配置 @see {@link org.hibernate.cfg.AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER}
 * <pre>
 *     spring:
 *       jpa:
 *         properties:
 *       hibernate.tenant_identifier_resolver: com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver
 * </pre>
 * hibernate tenant Identifier
 * 租户id获取方法
 */
public class CustomMultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return Optional.ofNullable(TenantIdentifierHelper.getTenant()).orElse(TenantIdentifierHelper.DEFAULT);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
