package com.ckontur.pkr.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

public abstract class WebConfig implements WebMvcConfigurer {

    @Autowired
    private BuildProperties buildProperties;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public VavrModule vavrModule() {
        return new VavrModule();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(vavrModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    public Docket swaggerApi() {
        Contact contact = new Contact("Цифровой контур", "https://c-kontur.com/", "main@c-kontur.com");
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(buildProperties.getGroup()))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(new ApiInfo(
                buildProperties.getName(), description(), buildProperties.getVersion(), null, contact,
                    null, null, Collections.emptyList()
            ))
            .securityContexts(List.of(SecurityContext.builder().securityReferences(securityReferences()).build()))
            .securitySchemes(List.of(new ApiKey("JWT", "Authorization", "header")));
    }

    private List<SecurityReference> securityReferences() {
        return List.of(new SecurityReference("JWT", new AuthorizationScope[]{
            new AuthorizationScope("global", "accessEverything")
        }));
    }

    public abstract String description();

}
