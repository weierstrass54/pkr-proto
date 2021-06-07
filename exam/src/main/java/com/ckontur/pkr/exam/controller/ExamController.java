package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.exam.model.DetailedExam;
import com.ckontur.pkr.exam.model.Exam;
import com.ckontur.pkr.exam.repository.ExamRepository;
import com.ckontur.pkr.exam.web.ExamRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Api(tags = {"Экзамены"})
@RequestMapping("/exam")
@RestController
@RequiredArgsConstructor
@Timed(value = "requests.exam", percentiles = {0.75, 0.9, 0.95, 0.99})
public class ExamController {
    private final ExamRepository examRepository;

    @GetMapping("/list/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public List<Exam> getAll() {
        return examRepository.findAll();
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public List<Exam> getAllByQualificationAndLevel(
        @RequestParam("qualificationId") Long qualificationId,
        @RequestParam("levelId") Long levelId
    ) {
        return examRepository.findAllByQualificationAndLevel(qualificationId, levelId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam getById(@PathVariable("id") Long id) {
        return examRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('EXAMINEE')")
    public DetailedExam getByExaminee(@AuthenticationPrincipal Principal principal) {
        throw new NotImplementedYetException();
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam create(@Valid @RequestBody ExamRequests.CreateExam exam) throws Throwable {
        return examRepository.create(exam)
            .map(e -> e.orElseThrow(() -> new CreateEntityException("Не удалось создать экзамен.")))
            .orElseThrow(t -> t instanceof DataIntegrityViolationException ?
                new IllegalArgumentException("Неверный параметр qualificationId или levelId.") : t);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam change(@PathVariable("id") Long id, ExamRequests.ChangeExam exam) throws Throwable {
        return examRepository.updateById(id, exam)
            .map(e -> e.orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден.")))
            .orElseThrow(t -> t instanceof DataIntegrityViolationException ?
                new IllegalArgumentException("Неверный параметр qualificationId или levelId.") : t);
    }

    @PostMapping("/{id}/addQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam addQuestionByIds(@PathVariable("id") Long id, @Valid @RequestBody ExamRequests.ExamQuestions examQuestions) throws Throwable {
        return examRepository.addQuestionsByIds(id, examQuestions.getQuestionIds())
            .orElseThrow(t -> t instanceof DataIntegrityViolationException ?
                new NotFoundException("Экзамен " + id + " не найден или не найден один из указанных вопросов.") : t);
    }

    @PostMapping("/{id}/removeQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam removeQuestionByIds(@PathVariable("id") Long id, @Valid @RequestBody ExamRequests.ExamQuestions examQuestions) {
        return examRepository.removeQuestionsByIds(id, examQuestions.getQuestionIds())
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam delete(Long id) {
        return examRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }
}
