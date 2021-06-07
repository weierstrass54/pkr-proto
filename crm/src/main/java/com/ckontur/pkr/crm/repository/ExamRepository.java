package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.exception.AuthenticationFailedException;
import com.ckontur.pkr.crm.model.Exam;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Repository
public class ExamRepository {
    private final WebClient webClient;

    public ExamRepository(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction loadBalancerFilter) {
        this.webClient = webClientBuilder.baseUrl("http://exam").filter(loadBalancerFilter).build();
    }

    public List<Exam> findAll() {
        String jweToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return webClient.get()
            .uri("/exam/list")
            .header("Authorization", "Bearer " + jweToken)
            .retrieve()
            .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                __ -> Mono.error(new AuthenticationFailedException("Запрос изменения пользователя не авторизован.")))
            .bodyToFlux(Exam.class)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
            .collectList()
            .block();
    }

}
