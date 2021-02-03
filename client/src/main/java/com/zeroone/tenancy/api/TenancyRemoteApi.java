package com.zeroone.tenancy.api;

import com.zeroone.tenancy.constants.TenancyApiConstants;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.dto.RestResult;
import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.enums.LoadBalanceStrategyEnum;
import com.zeroone.tenancy.properties.TenancyClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TenancyRemoteApi {




    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(new HttpHeaders());

    private final TenancyClientConfig tenancyClientConfig;

    private final RestTemplate restTemplate;

    private LoadBalanceStrategy loadBalanceStrategy;

    private Supplier<String> url;


    public TenancyRemoteApi(TenancyClientConfig tenancyClientConfig, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {

        this.tenancyClientConfig = tenancyClientConfig;
        this.restTemplate = new RestTemplate();
        //检测是否启用ribbon
        if (tenancyClientConfig.getEnableDiscoverClient() && tenancyClientConfig.getEnableDiscoverClient()) {

            restTemplateCustomizers.ifAvailable(customizers -> {
                String serverName = tenancyClientConfig.getServerName();
                if (StringUtils.isBlank(serverName)){
                    throw new IllegalStateException("tenancy server config [tenancy.client.server-name] not found,check config please");
                }
                for (RestTemplateCustomizer customizer : customizers) {
                    customizer.customize(restTemplate);
                }
                this.url = this.tenancyClientConfig::getServerName;
            });

        }else {

            List<String> serverUrls = tenancyClientConfig.getServerUrls();
            if (CollectionUtils.isEmpty(serverUrls)){
                throw new IllegalStateException("tenancy server config [tenancy.client.server-urls] not found,check config please");
            }
            List<String> urls = new ArrayList<>();
            serverUrls.forEach(url -> {
                urls.add(url.replace("http：//","").trim());
            });

            LoadBalanceStrategyEnum strategy = tenancyClientConfig.getStrategy();
            if (strategy == null){
                this.loadBalanceStrategy = new RoundStrategy(urls);
            }else if (strategy.equals(LoadBalanceStrategyEnum.ROUND)){
                this.loadBalanceStrategy = new RoundStrategy(urls);
            }else {
                this.loadBalanceStrategy = new RandomStrategy(urls);
            }
            this.url = () -> loadBalanceStrategy.select();
        }



    }

    public RestResult<List<DataSourceInfo>> getAvailableConfigInfo() {

        return restTemplate.exchange(getRequestUri(TenancyApiConstants.Query.QUERY_ALL_DATA_SOURCE, tenancyClientConfig.getInstantName()), HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<RestResult<List<DataSourceInfo>>>() {
        }).getBody();


    }

    public RestResult<DataSourceInfo> queryDataSource(String tenantCode){

        String requestUri = getRequestUri(TenancyApiConstants.Query.QUERY_TENANT_DATA_SOURCE, tenantCode, tenancyClientConfig.getInstantName(),"mysql");
        return  restTemplate.exchange(requestUri, HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<RestResult<DataSourceInfo>>() {
        }).getBody();

    }

    /**
     * 推送租户信息
     */
    public RestResult<Void> pushDataSourceMetrics(TenancyMetricsDTO tenancyMetricsDTO){

        String requestUri = getRequestUri(TenancyApiConstants.Query.PUSH_DATA_SOURCE_METRICS);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());
        HttpEntity<TenancyMetricsDTO> tenancyMetricsDTOHttpEntity = new HttpEntity<>(tenancyMetricsDTO,headers);
        return restTemplate.exchange(requestUri, HttpMethod.POST, tenancyMetricsDTOHttpEntity, new ParameterizedTypeReference<RestResult<Void>>() {
        }).getBody();
    }

    private String getRequestUri(String uri, Object... replace) {

        if (!uri.startsWith("/")){
            uri = "/"+ uri;
        }

        return String.format("http://" + url.get() + uri, replace);
    }


    interface LoadBalanceStrategy {

        String select();
    }


    static class RoundStrategy implements LoadBalanceStrategy {


        private final String[] serverUrl;


        private final AtomicInteger count = new AtomicInteger(0);

        public RoundStrategy(List<String> serverUrl) {

            this.serverUrl = serverUrl.toArray(new String[0]);
        }

        @Override
        public String select() {
            return serverUrl[(serverUrl.length - 1) % count.incrementAndGet()];
        }
    }


    static class RandomStrategy implements LoadBalanceStrategy {

        private final String[] serverUrl;

        private final Random random = new Random();

        public RandomStrategy(List<String> serverUrl) {
            this.serverUrl = serverUrl.toArray(new String[0]);
        }

        @Override
        public String select() {
            return serverUrl[random.nextInt(serverUrl.length - 1)];
        }
    }
}
