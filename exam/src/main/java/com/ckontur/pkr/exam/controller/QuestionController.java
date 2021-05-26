package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.repository.QuestionRepository;
import com.ckontur.pkr.exam.web.QuestionRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"Экзаменационные вопросы"})
@RequestMapping("/question")
@RestController
@RequiredArgsConstructor
@Timed(value = "requests.question", percentiles = {0.75, 0.9, 0.95, 0.99})
public class QuestionController {
    private final QuestionRepository questionRepository;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @PostMapping("/singleOrMultiple")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Question createSingleOrMultiple(
        @Valid @RequestBody QuestionRequests.CreateSingleOrMultipleQuestion question
    ) {
       return questionRepository.create(question)
           .orElseThrow(() -> new CreateEntityException("Не удалось создать вопрос."));
    }

    @PostMapping("/sequence")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Question createSequence(
        @Valid @RequestBody QuestionRequests.CreateSequenceQuestion question
    ) {
        return questionRepository.create(question)
            .orElseThrow(() -> new CreateEntityException("Не удалось создать вопрос."));
    }

    @PostMapping("/match")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
    public Question createMatch(
        @Valid @RequestBody QuestionRequests.CreateMatchQuestion question
    ) {
        return questionRepository.create(question)
            .orElseThrow(() -> new CreateEntityException("Не удалось создать вопрос."));
    }

}
