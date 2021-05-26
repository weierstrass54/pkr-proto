package com.ckontur.pkr.record.repository;

import com.ckontur.pkr.common.exception.AuthenticationFailedException;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.record.model.*;
import com.ckontur.pkr.record.web.UserRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Repository
public class CrmRepository {
    private final WebClient webClient;

    @Autowired
    public CrmRepository(WebClient.Builder webClientBuilder, @Value("${pkr.services.crm.url}") String crmUrl) {
        this.webClient = webClientBuilder.baseUrl(crmUrl).build();
    }

    public List<Region> getRegions() {
        return fetchRawList("/region/list", Region.class, Duration.ofSeconds(5L),
            () -> new AuthenticationFailedException("Запрос списка регионов не авторизован."));
    }

    public List<Qualification> getQualifications() {
        return fetchRawList("/qualification/list", Qualification.class, Duration.ofSeconds(5L),
            () -> new AuthenticationFailedException("Запрос списка квалификаций не авторизован."));
    }

    public List<Level> getLevels() {
        return fetchRawList("/level/list", Level.class, Duration.ofSeconds(5L),
            () -> new AuthenticationFailedException("Запрос списка уровней не авторизован."));
    }

    public List<Assessment> getAssessments() {
        return fetchRawList("/assessment/list", Assessment.class, Duration.ofSeconds(5L),
            () -> new AuthenticationFailedException("Запрос списка ЦОК не авторизован."));
    }

    public List<Schedule> getScheduleByAssessment(Long assessmentId) {
        return fetchRawList("/schedule/" + assessmentId, Schedule.class, Duration.ofSeconds(5L),
            () -> new AuthenticationFailedException("Запрос расписания не авторизован."));
    }

    public Optional<User> getUserById(Long id) {
        return Optional.empty();
    }

    public Optional<User> create(UserRequests.CreateUser userData) {
        return Optional.empty();
    }

    public Optional<User> updateUserById(Long id, UserRequests.ChangeUser usedData) {
        return Optional.empty();
    }

    public Optional<User> deleteUserById(Long id) {
        return Optional.empty();
    }

    private  <T> List<T> fetchRawList(String uri, Class<T> clazz, Duration timeout, Supplier<Throwable> onForbidden) {
        String jwtToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        try {
            return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer" + jwtToken)
                .retrieve()
                .onStatus(httpStatus -> httpStatus == HttpStatus.FORBIDDEN,
                    __ -> Mono.error(onForbidden.get()))
                .bodyToFlux(clazz)
                .collectList()
                .block(timeout);
        }
        catch (Throwable t) {
            log.error("{}", t.getMessage(), t);
            return Collections.emptyList();
        }
    }

}
