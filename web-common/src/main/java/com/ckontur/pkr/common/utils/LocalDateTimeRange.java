package com.ckontur.pkr.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeRange {
    private LocalDateTime from;
    private LocalDateTime to;
    private boolean includeFrom;
    private boolean includeTo;
}
