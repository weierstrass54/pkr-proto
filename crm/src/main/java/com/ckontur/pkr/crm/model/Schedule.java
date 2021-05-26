package com.ckontur.pkr.crm.model;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Schedule {
    private final Long id;
    private final Long assessmentId;
    private final LocalDateTimeRange dateTimeRange;
    private final Integer availableCount;
}
