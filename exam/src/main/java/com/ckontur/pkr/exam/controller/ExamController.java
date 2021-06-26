package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.exam.model.DetailedExam;
import com.ckontur.pkr.exam.model.Exam;
import com.ckontur.pkr.exam.repository.DetailedExamRepository;
import com.ckontur.pkr.exam.web.ExamRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Api(tags = {"Экзамены"})
@RequestMapping("/exam")
@RestController
@RequiredArgsConstructor
@Timed(value = "requests.exam", percentiles = {0.75, 0.9, 0.95, 0.99})
public class ExamController {
    private final DetailedExamRepository examRepository;

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
            .getOrElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('EXAMINEE')")
    public DetailedExam getByExaminee(@AuthenticationPrincipal User user) {
        throw new NotImplementedYetException();
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam create(@Valid @RequestBody ExamRequests.CreateExam exam) throws Throwable {
        return examRepository.create(exam)
            .getOrElseThrow(t -> Match(t).of(
                Case($(instanceOf(DataIntegrityViolationException.class)), __ ->
                    new IllegalArgumentException("Указан несуществующий параметр qualificationId или levelId.")),
                Case($(), e -> new CreateEntityException("Не удалось создать экзамен.", e))
            ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam change(@PathVariable("id") Long id, ExamRequests.ChangeExam exam) {
        return examRepository.updateById(id, exam)
            .getOrElseThrow(t -> Match(t).of(
                Case($(instanceOf(DataIntegrityViolationException.class)), __ ->
                    new IllegalArgumentException("Указан несуществующий параметр qualificationId или levelId.")),
                Case($(), e -> new CreateEntityException("Не удалось обновить экзамен.", e))
            ))
            .getOrElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @PostMapping("/{id}/addQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam addQuestionByIds(@PathVariable("id") Long id, @Valid @RequestBody ExamRequests.ExamQuestions examQuestions) throws Throwable {
        return examRepository.addQuestionsByIds(id, examQuestions.getQuestionIds())
            .getOrElseThrow(t -> Match(t).of(
                Case($(instanceOf(DataIntegrityViolationException.class)), __ ->
                    new IllegalArgumentException("Указан несуществующий экзамен или несуществующий вопрос.")),
                Case($(), () -> t)
            ));
    }

    @PostMapping("/{id}/removeQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam removeQuestionByIds(@PathVariable("id") Long id, @Valid @RequestBody ExamRequests.ExamQuestions examQuestions) {
        return examRepository.removeQuestionsByIds(id, examQuestions.getQuestionIds())
            .getOrElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public DetailedExam delete(Long id) {
        return examRepository.deleteById(id)
            .getOrElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }
}
