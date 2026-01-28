package com.rainer.cloudmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudmallSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudmallSearchApplication.class, args);
	}

}
