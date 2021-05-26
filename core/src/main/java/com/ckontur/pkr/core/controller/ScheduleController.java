package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import com.ckontur.pkr.core.model.Schedule;
import com.ckontur.pkr.core.service.ScheduleService;
import com.ckontur.pkr.core.web.ScheduleRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api(tags = {"Расписание экзаменов"})
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM','OPERATOR')")
@Timed(value = "requests.schedule", percentiles = {0.75, 0.9, 0.95, 0.99})
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/{id}")
    public List<Schedule> getAllByAssessmentId(
        @PathVariable("id") Long id,
        @RequestParam(value = "period", required = false)
        @Min(value = 1, message = "Расписание можно запросить за положительное кол-во дней.")
        Integer days
    ) {
        return Optional.ofNullable(days)
            .map(Duration::ofDays)
            .map(__ -> scheduleService.getAllByAssessmentId(id, __))
            .orElseGet(() -> scheduleService.getAllByAssessmentId(id));
    }

    @PostMapping(value = "/overwrite")
    public List<Schedule> overwrite(@RequestBody @Valid List<ScheduleRequests.CreateSchedule> schedule) {
        return scheduleService.overwrite(schedule);
    }

    @PostMapping(value = "/import")
    public List<Schedule> append(@RequestBody @Valid List<ScheduleRequests.CreateSchedule> schedule) {
        return scheduleService.append(schedule);
    }

    @DeleteMapping("/{id}")
    public List<Schedule> deleteByAssessmentId(@PathVariable("id") Long id) {
        return scheduleService.deleteByAssessmentId(id);
    }

}
