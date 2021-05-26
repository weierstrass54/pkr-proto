package com.ckontur.pkr.common.config;

import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudConfig {
    @Bean
    public IRule ribbonRule() {
        return new BestAvailableRule();
    }
}
