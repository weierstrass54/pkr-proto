package com.ckontur.pkr.crm.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateOrUpdateScheduleRequest {
    private Long assessmentId;

    @NotNull
    @JsonFormat(pattern = "YYYY-mm-dd HH:mm")
    private LocalDateTime dateTime;
}
