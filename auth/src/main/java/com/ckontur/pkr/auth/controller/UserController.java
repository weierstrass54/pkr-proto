package com.ckontur.pkr.auth.controller;

import com.ckontur.pkr.auth.service.UserService;
import com.ckontur.pkr.auth.web.ChangeUserRequest;
import com.ckontur.pkr.auth.web.CreateUserRequest;
import com.ckontur.pkr.common.exception.InvalidArgumentException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.model.User;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Пользователи"})
@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('ADMIN','INTERNAL','CRM')")
@RequiredArgsConstructor
@Timed(value = "requests.user", percentiles = {0.75, 0.9, 0.95, 0.99})
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id) {
        return userService.getById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @PostMapping("/")
    public User create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request)
            .orElseThrow(() -> new InvalidArgumentException("Пользователь с логином " + request.getLogin() + " уже существует."));
    }

    @PutMapping("/{id}")
    public User change(@PathVariable("id") Long id, @Valid @RequestBody ChangeUserRequest request) {
        return userService.updateById(id, request)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable("id") Long id) {
        return userService.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

}
