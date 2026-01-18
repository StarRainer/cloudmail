package com.rainer.cloudmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.rainer.cloudmall.coupon.dao")
@EnableDiscoveryClient()
@EnableFeignClients("com.rainer.cloudmall.coupon.feign")
public class CloudmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudmallCouponApplication.class, args);
    }

}
