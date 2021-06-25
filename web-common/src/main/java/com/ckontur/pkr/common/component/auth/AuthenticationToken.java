package com.ckontur.pkr.common.component.auth;

import com.ckontur.pkr.common.model.User;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    public AuthenticationToken(Tuple2<String, User> auth) {
        super(auth._2, auth._1, auth._2.getAuthorities());
    }

}
