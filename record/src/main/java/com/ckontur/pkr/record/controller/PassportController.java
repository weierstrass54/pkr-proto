package com.ckontur.pkr.record.controller;

import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.record.model.Passport;
import com.ckontur.pkr.record.repository.PassportRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Паспортные данные"})
@RestController
@RequestMapping("/passport")
@RequiredArgsConstructor
public class PassportController {
    private final PassportRepository passportRepository;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('EXAMINEE')")
    public Passport getByUser(@AuthenticationPrincipal User user) {
        return null;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN', 'CRM')")
    public Passport getByUserId(@PathVariable("id") Long userId) {
        return null;
    }

    @PostMapping("/")
    public Passport create() {

    }

    @DeleteMapping("/")
    @PreAuthorize("hasAuthority('EXAMINEE')")
    public Passport delete() {

    }

    /*
    public Optional<Passport> getByUser(RecordUser user) {
        return jdbcTemplate.query("SELECT * FROM passports WHERE user_id = ?",
            PassportMapper.INSTANCE, user.getId()).stream().findAny();
    }

    public Optional<Passport> create(RecordUser user, PassportRequests.PassportCreate passport) {
        return jdbcTemplate.query("INSERT INTO passports(user_id, series, number, issued_by, issued_at) " +
            "VALUES (?, ?, ?, ?, ?)", PassportMapper.INSTANCE, user.getId(), passport.getSeries(), passport.getNumber(),
                passport.getIssuedBy(), Timestamp.valueOf(passport.getIssuedBy())).stream().findAny();
    }

    public Optional<Passport> deleteByUser(RecordUser user) {
        return jdbcTemplate.query("DELETE FROM passports WHERE user_id = ? RETURNING *",
            PassportMapper.INSTANCE, user.getId()).stream().findAny();
    }
     */
}
