package com.zeroone.tenancy;

import com.zeroone.tenancy.annotation.EnableTenancyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
@EnableTenancyServer
@SpringBootApplication
@EnableJpaRepositories("com.zeroone")
@EntityScan("com.zeroone")
@EnableFeignClients
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class,args);
    }
}
