package com.ckontur.pkr.record.model;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Long id;
    private Long assessmentId;
    private LocalDateTimeRange dateTimeRange;
    private Integer availableCount;
}
