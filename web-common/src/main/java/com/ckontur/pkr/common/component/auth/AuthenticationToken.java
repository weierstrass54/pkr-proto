package com.ckontur.pkr.common.component.auth;

import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.common.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    public AuthenticationToken(Pair<String, User> auth) {
        super(auth.getRight(), auth.getLeft(), auth.getRight().getAuthorities());
    }

}
