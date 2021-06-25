package com.ckontur.pkr.common.component.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.ckontur.pkr.common.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtVerifier {
    private final ObjectMapper objectMapper;
    private final JWTVerifier jwtVerifier;

    public JwtVerifier(Algorithm algorithm, ObjectMapper objectMapper) {
        jwtVerifier = JWT.require(algorithm)
            .withIssuer("ckontur.pkr")
            .build();
        this.objectMapper = objectMapper;
    }

    public Option<Tuple2<String, User>> verify(String token) {
        return Try.of(() -> {
            Claim authenticated = jwtVerifier.verify(token).getClaim("authenticated");
            User user = objectMapper.readValue(authenticated.asString(), User.class);
            return Option.of(new Tuple2<>(token, user));
        }).getOrElseGet(e -> {
            log.error("{}", e.getMessage(), e);
            return Option.none();
        });
    }
}
