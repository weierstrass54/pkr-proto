package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.core.model.Exam;
import com.ckontur.pkr.core.repository.ExamRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"Экзамены ПКР"})
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
public class ExamController {
    private final ExamRepository examRepository;

    @GetMapping("/list")
    public List<Exam> getExams() {
        return examRepository.getAll();
    }

    @PostMapping("/")
    public Exam create() {
        return examRepository.create()
            .orElseThrow(() -> new CreateEntityException("Не удалось создать экзамен."));
    }

    @PutMapping("/{id}")
    public Exam change(@PathVariable("id") Long id) {
        return examRepository.updateById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Exam delete(@PathVariable("id") Long id) {
        return examRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Экзамен " + id + " не найден."));
    }

}
