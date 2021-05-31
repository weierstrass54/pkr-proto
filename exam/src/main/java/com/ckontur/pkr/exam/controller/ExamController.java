package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.exam.model.Exam;
import com.ckontur.pkr.exam.repository.ExamRepository;
import com.ckontur.pkr.exam.web.ExamRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(tags = {"Экзамены"})
@RequestMapping("/exam")
@RestController
@RequiredArgsConstructor
@Timed(value = "requests.exam", percentiles = {0.75, 0.9, 0.95, 0.99})
public class ExamController {
    private final ExamRepository examRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam getById(@PathVariable("id") Long id) {
        return examRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam getAll() {
        throw new NotImplementedYetException();
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('EXAMINEE')")
    public Exam getByExaminee(@AuthenticationPrincipal Principal principal) {
        throw new NotImplementedYetException();
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam create(@Valid @RequestBody ExamRequests.CreateExam exam) {
        return examRepository.create(exam)
            .orElseThrow(() -> new CreateEntityException("Не удалось создать экзамен."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam change(@PathVariable("id") Long id, ExamRequests.ChangeExam exam) {
        return examRepository.updateById(id, exam)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    //@PostMapping
    //addQuestion

    @PostMapping("/{id}/addQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam addQuestionByIds(@PathVariable("id") Long id) {
        return examRepository.addQuestionByIds()
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @PostMapping("/{id}/removeQuestions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam addQuestionByIds(@PathVariable("id") Long id) {
        return examRepository.removeQuestionByIds()
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Exam delete(Long id) {
        return examRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }
}
