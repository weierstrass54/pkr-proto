package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.exception.AuthenticationFailedException;
import com.ckontur.pkr.common.exception.EntityAlreadyExistsException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.crm.web.UserRequests;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

@Repository
public class AuthRepository {
    private final WebClient webClient;

    @Autowired
    public AuthRepository(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction loadBalancerFilter) {
        this.webClient = webClientBuilder.baseUrl("http://auth/").filter(loadBalancerFilter).build();
    }

    public Optional<User> getById(Long id) {
        String jwtToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return webClient.get()
            .uri("/user/" + id)
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                __ -> Mono.error(new AuthenticationFailedException("Запрос изменения пользователя не авторизован.")))
            .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
                __ -> Mono.error(new NotFoundException("Пользователь не найден.")))
            .bodyToMono(User.class)
            .blockOptional();
    }

    public Optional<User> create(UserRequests.CreateUser userData) {
        String jwtToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return webClient.post()
            .uri("/user")
            .header("Authorization", "Bearer " + jwtToken)
            .body(Mono.just(new AuthUserRequest(
                userData.getLogin(), userData.getPassword(), Set.of(userData.getAuthority())
            )), AuthUserRequest.class)
            .retrieve()
            .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                __ -> Mono.error(new AuthenticationFailedException("Запрос создания пользователя не авторизован.")))
            .onStatus(httpStatus -> httpStatus == HttpStatus.CONFLICT,
                __ -> Mono.error(new EntityAlreadyExistsException("Пользователь уже существует.")))
            .bodyToMono(User.class)
            .blockOptional();
    }

    public Optional<User> updateById(Long id, UserRequests.ChangeUser userData) {
        String jwtToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return webClient.put()
            .uri("/user/" + id)
            .header("Authorization", "Bearer " + jwtToken)
            .body(Mono.just(new AuthUserRequest(
                userData.getLogin(), userData.getPassword(), Set.of(userData.getAuthority())
            )), AuthUserRequest.class)
            .retrieve()
            .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                __ -> Mono.error(new AuthenticationFailedException("Запрос изменения пользователя не авторизован.")))
            .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
                __ -> Mono.error(new NotFoundException("Пользователь не найден.")))
            .bodyToMono(User.class)
            .blockOptional();
    }

    public Optional<User> deleteById(Long id) {
        String jwtToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return webClient.delete()
            .uri("/user/" + id)
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                __ -> Mono.error(new AuthenticationFailedException("Запрос удаления пользователя не авторизован.")))
            .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
                __ -> Mono.error(new NotFoundException("Пользователь не найден.")))
            .bodyToMono(User.class)
            .blockOptional();
    }

    @Data
    @AllArgsConstructor
    private static class AuthUserRequest {
        private String login;
        private String password;
        private Set<Authority> authorities;
    }

}
