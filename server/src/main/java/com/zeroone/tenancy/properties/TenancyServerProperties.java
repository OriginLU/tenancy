package com.zeroone.tenancy.properties;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
@Data
@Accessors(chain = true)
@ToString

@Configuration
@ConfigurationProperties(prefix = "tenancy.server", ignoreInvalidFields = true)
public class TenancyServerProperties {


}
