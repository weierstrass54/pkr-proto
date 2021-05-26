package com.ckontur.pkr.core.model;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Schedule {
    private final Long id;
    private final Exam exam;
    private final LocalDateTimeRange dateTimeRange;
    private final Integer availableCount;
}
