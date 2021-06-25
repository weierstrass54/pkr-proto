package com.ckontur.pkr.crm.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.model.Page;
import com.ckontur.pkr.common.request.PageRequest;
import com.ckontur.pkr.crm.model.CrmUser;
import com.ckontur.pkr.crm.service.UserService;
import com.ckontur.pkr.crm.web.UserRequests;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Пользователи"})
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','INTERNAL','CRM')")
public class UserController {
    private final UserService userService;

    @GetMapping("/search")
    public Page<CrmUser> search(
        @RequestParam(value = "search", required = false) String searchString,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "50") int size,
        @RequestParam(value = "sort", required = false, defaultValue = "ASC") String sort
    ) {
        return userService.search(searchString, PageRequest.of(page, size, PageRequest.Direction.of(sort)));
    }

    @GetMapping("/{id}")
    public CrmUser getById(@PathVariable("id") Long id) {
        return userService.getById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @PostMapping("/")
    public CrmUser create(@Valid @RequestBody UserRequests.CreateUser userData) {
        return userService.create(userData)
            .orElseThrow(() -> new CreateEntityException("Не удалось создать пользователя."));
    }

    @PutMapping("/{id}")
    public CrmUser updateById(@PathVariable("id") Long id, UserRequests.ChangeUser userData) {
        return userService.updateById(id, userData)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public CrmUser deleteById(@PathVariable("id") Long id) {
        return userService.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден."));
    }

}
