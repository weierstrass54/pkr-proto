package com.ckontur.pkr.common.component.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter implements Filter {
    private static final String AUTH_HEADER = "Authorization";

    @Autowired
    private JwtVerifier jwtVerifier;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        extractToken((HttpServletRequest) request)
            .flatMap(jwtVerifier::verify)
            .map(AuthenticationToken::new)
            .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        chain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_HEADER))
            .filter(bearer -> !bearer.isEmpty() && bearer.startsWith("Bearer "))
            .map(bearer -> bearer.substring(7));
    }

}
