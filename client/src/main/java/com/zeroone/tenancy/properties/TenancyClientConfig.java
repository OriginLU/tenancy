package com.zeroone.tenancy.properties;

import com.zeroone.tenancy.enums.LoadBalanceStrategyEnum;
import com.zeroone.tenancy.utils.DockerUtils;
import com.zeroone.tenancy.utils.NetUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "tenancy.client", ignoreInvalidFields = true)
public class TenancyClientConfig implements BeanPostProcessor, EnvironmentAware {


    public final static long DEFAULT_RETRIEVE_TIME = TimeUnit.HOURS.toMillis(1L);

    private final static  String INSTANCE_NAME_KEY = "spring.application.name";

    private final Interceptor interceptor = new Interceptor();

    private Environment environment;
    /**
     * 数据源服务名称
     */
    private String serverName;

    /**
     * 实例名称
     */
    private String instantName;

    /**
     * 空闲回收时间单位：分[min]
     */
    private Long retrieveTime;

    /**
     * 服务端口
     */
    private Integer serverPort;

    /**
     * ip
     */
    private String ip;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 租户服务地址
     */
    private List<String> serverUrls;

    /**
     * 负载均衡策略
     */
    private LoadBalanceStrategyEnum strategy;

    /**
     * 初始化同步/异步
     */
    private Boolean sync;


    /**
     * 是否启用eureka客户端
     */
    private Boolean isEnableEurekaClient;

    /**
     * 是否启用服务发现客户端
     */
    private Boolean isEnableDiscoverClient;

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public Boolean getEnableDiscoverClient() {
        return isEnableDiscoverClient;
    }

    public Boolean getEnableEurekaClient() {
        return isEnableEurekaClient;
    }

    public LoadBalanceStrategyEnum getStrategy() {
        return strategy;
    }

    public void setStrategy(LoadBalanceStrategyEnum strategy) {
        this.strategy = strategy;
    }

    public List<String> getServerUrls() {
        return serverUrls;
    }

    public void setServerUrls(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getIp() {
        return ip;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getInstantName() {
        return instantName;
    }

    public void setInstantName(String instantName) {
        this.instantName = instantName;
    }

    public Long getRetrieveTime() {
        return retrieveTime;
    }

    public void setRetrieveTime(Long retrieveTime) {
        this.retrieveTime = retrieveTime;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!StringUtils.hasText(instantName)){
            this.instantName = getEnvironment().getProperty(INSTANCE_NAME_KEY);
        }

        if (DockerUtils.isDocker()) {
            this.ip = DockerUtils.getDockerHost();
        } else {
            this.ip = NetUtils.getLocalAddress();
        }

        this.serverPort = Integer
                .valueOf(getEnvironment().getProperty("server.port", getEnvironment().getProperty("port", "8080")));
        //配置实例ID
        this.instanceId = UUID.randomUUID().toString().replace("-", "");

        this.isEnableEurekaClient = getEnvironment().getProperty("eureka.client.enabled",Boolean.class,Boolean.FALSE);

        this.isEnableDiscoverClient = getEnvironment().getProperty("spring.cloud.discovery.enabled", Boolean.class, Boolean.FALSE);

        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static class Interceptor{

        private List<String> excludeUrls;

        public List<String> getExcludeUrls() {
            return excludeUrls;
        }

        public void setExcludeUrls(List<String> excludeUrls) {
            this.excludeUrls = excludeUrls;
        }

        @Override
        public String toString() {
            return "Interceptor{" +
                    "excludeUrls=" + excludeUrls +
                    '}';
        }
    }

}
