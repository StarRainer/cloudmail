package com.rainer.cloudmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.rainer.cloudmall.order.dao")
@EnableDiscoveryClient
@EnableFeignClients("com.rainer.cloudmall.order.feign")
public class CloudmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudmallOrderApplication.class, args);
    }

}
