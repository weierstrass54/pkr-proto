package com.ckontur.pkr.common.component.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.common.utils.Pair;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public Optional<Pair<String, User>> verify(String token) {
        try {
            Claim authenticated = jwtVerifier.verify(token).getClaim("authenticated");
            User user = objectMapper.readValue(authenticated.asString(), User.class);
            return Optional.of(Pair.of(token, user));
        }
        catch (Throwable t) {
            log.error("{}", t.getMessage(), t);
            return Optional.empty();
        }
    }
}
