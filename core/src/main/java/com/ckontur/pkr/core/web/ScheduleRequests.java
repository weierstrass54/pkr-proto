package com.ckontur.pkr.core.web;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ScheduleRequests {

    @Data
    public static class CreateSchedule {
        @NotNull(message = "Поле assessmentId должно быть непустым.")
        private Integer assessmentId;
        @NotNull(message = "Поле exam должно быть непустым.")
        private Integer examId;
        @NotEmpty(message = "Список временных периодов не должен быть пуст.")
        private List<LocalDateTimeRange> periods;
    }

}
