package com.ckontur.pkr.auth.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.common.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final Algorithm algorithm;
    private final ObjectMapper objectMapper;

    @Value("${jwt.lifetime.internal:24}")
    private int internalLifetime;

    @Value("${jwt.lifetime.operator:10}")
    private int operatorLifetime;

    @Value("${jwt.lifetime.default:3}")
    private int defaultLifetime;

    public <T extends User> String generateToken(T user) {
        JWTCreator.Builder jwtBuilder = JWT.create()
            .withIssuer("ckontur.pkr")
            .withClaim("authenticated", asClaim(user));
        getExpiredAt(user).ifPresent(jwtBuilder::withExpiresAt);
        return jwtBuilder.sign(algorithm);
    }

    private <T extends User> String asClaim(T user) {
        return Try.of(() -> objectMapper.writeValueAsString(user)).getOrElse("");
    }

    private <T extends User> Optional<Date> getExpiredAt(T user) {
        if (user.getAuthorities().contains(Authority.ADMIN)) {
            return Optional.empty();
        }
        if (user.getAuthorities().contains(Authority.INTERNAL)) {
            return Optional.of(DateUtils.of(LocalDateTime.now().plusHours(internalLifetime)));
        }
        if (user.getAuthorities().contains(Authority.OPERATOR)) {
            return Optional.of(DateUtils.of(LocalDateTime.now().plusHours(operatorLifetime)));
        }
        return Optional.of(DateUtils.of(LocalDateTime.now().plusHours(defaultLifetime)));
    }

}
