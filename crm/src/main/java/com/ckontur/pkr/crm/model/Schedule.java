package com.ckontur.pkr.crm.model;

import com.ckontur.pkr.common.utils.Interval;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Schedule {
    private final Long id;
    private final Long assessmentId;
    private final Long examId;
    private final Interval<LocalDateTime> interval;
}
