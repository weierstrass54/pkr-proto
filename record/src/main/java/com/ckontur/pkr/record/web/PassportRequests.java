package com.ckontur.pkr.record.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

public class PassportRequests {
    @Data
    public static class PassportCreate {
        private Integer series;
        private Integer number;
        private String issuedBy;
        @JsonFormat(pattern = "YYYY-mm-dd")
        private LocalDate issuedAt;
    }
}
