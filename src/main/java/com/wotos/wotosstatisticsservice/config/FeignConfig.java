package com.wotos.wotosstatisticsservice.config;

import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder springMvcContract() {
        return Feign.builder().contract(new SpringMvcContract());
    }

    @Bean
    Logger.Level feignLoggerLever() {
        return Logger.Level.FULL;
    }

}
