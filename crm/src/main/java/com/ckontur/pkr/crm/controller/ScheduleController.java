package com.ckontur.pkr.crm.controller;

import com.ckontur.pkr.crm.service.ScheduleService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Расписание экзаменов"})
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
public class ScheduleController {
    private final ScheduleService scheduleService;


    /*
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

    @PostMapping(value = "/overwrite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Schedule> overwrite(@RequestBody Map<Long, List<LocalDateTimeInterval>> schedule) {
        return scheduleService.overwrite(schedule);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Schedule> append(@RequestBody Map<Long, List<LocalDateTimeInterval>> schedule) {
        return scheduleService.append(schedule);
    }

    @DeleteMapping("/{id}")
    public List<Schedule> deleteByAssessmentId(@PathVariable("id") Long id) {
        return scheduleService.deleteByAssessmentId(id);
    }
     */

}
