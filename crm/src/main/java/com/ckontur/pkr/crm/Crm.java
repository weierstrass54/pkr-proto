package com.ckontur.pkr.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Crm {
    public static void main(String[] args) {
        SpringApplication.run(Crm.class, args);
    }
}
