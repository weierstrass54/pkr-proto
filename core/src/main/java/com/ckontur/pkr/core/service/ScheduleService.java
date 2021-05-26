package com.ckontur.pkr.core.service;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import com.ckontur.pkr.core.model.Schedule;
import com.ckontur.pkr.core.repository.ScheduleRepository;
import com.ckontur.pkr.core.web.ScheduleRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getAllByAssessmentId(Long assessmentId) {
        return scheduleRepository.getAllByAssessmentId(assessmentId);
    }

    public List<Schedule> getAllByAssessmentId(Long assessmentId, Duration duration) {
        return scheduleRepository.getAllByAssessmentId(assessmentId, duration);
    }

    public List<Schedule> append(List<ScheduleRequests.CreateSchedule> schedule) {
        LocalDateTime now = LocalDateTime.now();
        return Optional.of(
                schedule.stream()
                .map(ScheduleRequests.CreateSchedule::getPeriods)
                .flatMap(List::stream)
                .anyMatch(range -> range.getFrom().isBefore(now))
        ).filter(hasExpired -> !hasExpired)
        .map(__ -> scheduleRepository.create(schedule))
        .orElseThrow(() -> new IllegalArgumentException("Найдено расписание, затрагивающее прошлое."));
    }

    @Transactional
    public List<Schedule> overwrite(List<ScheduleRequests.CreateSchedule> schedule) {
        scheduleRepository.deleteAllNonExpired();
        return append(schedule);
    }

    public List<Schedule> deleteByAssessmentId(Long assessmentId) {
        return scheduleRepository.deleteByAssessmentId(assessmentId);
    }
}
