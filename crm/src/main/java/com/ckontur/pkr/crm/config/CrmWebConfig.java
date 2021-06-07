package com.ckontur.pkr.crm.config;

import com.ckontur.pkr.common.config.WebConfig;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableScheduling
@Configuration
@ComponentScan(basePackages = "com.ckontur.pkr.common.*")
public class CrmWebConfig extends WebConfig {
    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Override
    public String description() {
        return "Модуль CRM.";
    }
}
