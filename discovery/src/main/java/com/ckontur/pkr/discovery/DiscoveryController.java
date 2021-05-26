package com.ckontur.pkr.discovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class DiscoveryController {
    private final WebClient.Builder webClientBuilder;

    @GetMapping("/auth")
    public String discovery() {
        return webClientBuilder.baseUrl("http://auth")
            .build()
            .post()
            .uri("/auth/authenticate")
            .body(Mono.just(new Req("123", "123")), Req.class)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    @Getter
    @RequiredArgsConstructor
    private static class Req {
        private final String login;
        private final String password;
    }
}
