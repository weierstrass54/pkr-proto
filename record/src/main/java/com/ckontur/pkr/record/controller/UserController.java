package com.ckontur.pkr.record.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.record.model.RecordUser;
import com.ckontur.pkr.record.service.UserService;
import com.ckontur.pkr.record.web.UserRequests;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Пользователи"})
@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNAL', 'CRM')")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public RecordUser getById(@PathVariable("id") Long id) {
        return userService.getById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @PostMapping("/")
    public RecordUser create(@Valid @RequestBody UserRequests.CreateUser userData) {
        return userService.create(userData)
            .orElseThrow(() -> new CreateEntityException("Не удалось содать пользователя."));
    }

    @PutMapping("/{id}")
    public RecordUser updateById(@PathVariable("id") Long id, @Valid @RequestBody UserRequests.ChangeUser userData) {
        return userService.updateById(id, userData)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public RecordUser deleteById(@PathVariable("id") Long id) {
        return userService.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

}
