package com.ckontur.pkr.exam.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class ReportRepository {
    private JdbcTemplate jdbcTemplate;


}
