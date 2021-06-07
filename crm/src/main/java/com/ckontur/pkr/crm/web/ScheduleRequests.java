package com.ckontur.pkr.crm.web;

import com.ckontur.pkr.common.utils.Interval;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleRequests {
    @Data
    public static class Create {
        private Long assessmentId;
        private Long examId;
        private List<Interval<LocalDateTime>> intervals;
    }
}
