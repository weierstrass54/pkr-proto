package com.ckontur.pkr.record.model;

import com.ckontur.pkr.common.utils.LocalDateTimeInterval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Long id;
    private Long assessmentId;
    private LocalDateTimeInterval dateTimeRange;
    private Integer availableCount;
}
