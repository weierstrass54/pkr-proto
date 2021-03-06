package com.ckontur.pkr.exam.config;

import com.ckontur.pkr.common.config.WebConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "com.ckontur.pkr.common.*")
public class ExamWebConfig extends WebConfig {
    @Override
    public String description() {
        return "Модуль экзаменов ПКР.";
    }
}
