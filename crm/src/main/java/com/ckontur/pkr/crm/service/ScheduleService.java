package com.ckontur.pkr.crm.service;

import com.ckontur.pkr.crm.model.Exam;
import com.ckontur.pkr.crm.model.Schedule;
import com.ckontur.pkr.crm.repository.ExamRepository;
import com.ckontur.pkr.crm.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ExamRepository examRepository;

    public List<Schedule> getAllByAssessmentId(Long assessmentId) {
        List<Long> examIds = examRepository.findAll().stream().map(Exam::getId).collect(Collectors.toList());
        return scheduleRepository.findAllByAssessmentId(assessmentId).stream()
            .filter(schedule -> examIds.contains(schedule.getExamId()))
            .collect(Collectors.toList());
    }

    // TODO: add schedule

    public List<Schedule> deleteAllByAssessmentIdAndExamId(Long assessmentId, Long examId) {
        return scheduleRepository.deleteAllByAssessmentIdAndExamId(assessmentId, examId);
    }

    public Optional<Schedule> deleteById(Long id) {
        return scheduleRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clear() {
        scheduleRepository.deleteExpired();
        scheduleRepository.deleteAllExceptIds(
            examRepository.findAll().stream().map(Exam::getId).collect(Collectors.toList())
        );
    }

    /*
    public List<Schedule> append(Map<Long, List<LocalDateTimeInterval>> schedule) {
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
    public List<Schedule> overwrite(Map<Long, List<LocalDateTimeInterval>> schedule) {
        scheduleRepository.deleteAllNonExpired();
        return append(schedule);
    }
     */
}
