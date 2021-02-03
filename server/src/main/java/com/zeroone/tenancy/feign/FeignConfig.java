package com.zeroone.tenancy.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;


public class FeignConfig {


    @Bean
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }

    @Bean
    public Logger.Level loggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public Logger logger(){
        return new CustomerFeignSlf4jLogger();
    }


}
