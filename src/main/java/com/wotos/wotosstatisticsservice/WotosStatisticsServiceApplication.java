package com.wotos.wotosstatisticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableJpaRepositories
@EnableFeignClients
public class WotosStatisticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WotosStatisticsServiceApplication.class, args);
	}

}
