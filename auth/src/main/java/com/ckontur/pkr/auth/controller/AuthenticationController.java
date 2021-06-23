package com.ckontur.pkr.auth.controller;

import com.ckontur.pkr.auth.service.AuthenticateService;
import com.ckontur.pkr.auth.web.AuthenticateRequest;
import com.ckontur.pkr.common.exception.AuthenticationFailedException;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"Аутентификация"})
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Timed(value = "requests.auth", percentiles = {0.75, 0.9, 0.95, 0.99})
public class AuthenticationController {
    private final AuthenticateService authenticateService;

    @PostMapping("/authenticate")
    public String authenticate(@Valid @RequestBody AuthenticateRequest request) {
        return authenticateService.authenticate(request.getLogin(), request.getPassword())
            .getOrElseThrow(() -> new AuthenticationFailedException("Пара логин/пароль неверна."));
    }

}
