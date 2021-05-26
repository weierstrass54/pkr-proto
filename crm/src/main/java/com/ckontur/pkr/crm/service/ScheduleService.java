package com.ckontur.pkr.crm.service;

import com.ckontur.pkr.common.exception.InvalidArgumentException;
import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import com.ckontur.pkr.crm.model.Schedule;
import com.ckontur.pkr.crm.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<Schedule> append(Map<Long, List<LocalDateTimeRange>> schedule) {
        LocalDateTime now = LocalDateTime.now();
        return Optional.of(
            schedule.values().stream()
                .flatMap(List::stream)
                .anyMatch(localDateTimeRange -> localDateTimeRange.getFrom().isBefore(now))
        )
        .filter(hasExpired -> !hasExpired)
        .map(__ -> scheduleRepository.create(schedule))
        .orElseThrow(() -> new IllegalArgumentException("Найдено расписание, затрагивающее прошлое."));
    }

    @Transactional
    public List<Schedule> overwrite(Map<Long, List<LocalDateTimeRange>> schedule) {
        scheduleRepository.deleteAllNonExpired();
        return append(schedule);
    }

    public List<Schedule> deleteByAssessmentId(Long assessmentId) {
        return scheduleRepository.deleteByAssessmentId(assessmentId);
    }
}
