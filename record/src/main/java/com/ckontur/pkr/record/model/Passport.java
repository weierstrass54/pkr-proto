package com.ckontur.pkr.record.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    @JsonIgnore
    private Long userId;
    private Integer series;
    private Integer number;
    private String issuedBy;
    private LocalDate issuedAt;
}
