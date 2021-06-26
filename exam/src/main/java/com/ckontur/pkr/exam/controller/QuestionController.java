package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.request.PageRequest;
import com.ckontur.pkr.exam.model.question.Question;
import com.ckontur.pkr.exam.repository.QuestionRepository;
import com.ckontur.pkr.exam.web.QuestionRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Экзаменационные вопросы"})
@RequestMapping("/question")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'CRM')")
@Timed(value = "requests.question", percentiles = {0.75, 0.9, 0.95, 0.99})
public class QuestionController {
    private final QuestionRepository questionRepository;

    @GetMapping("/{examId}/list")
    public List<Question> findAllByExamId(@PathVariable Long examId) {
        return questionRepository.findAllByExamIdWithAnswers(examId);
    }

    @PostMapping("/singleOrMultiple")
    public Question createSingleOrMultiple(@Valid @RequestBody QuestionRequests.CreateSingleOrMultipleQuestion question) {
       return questionRepository.create(question)
           .getOrElseThrow(t -> new CreateEntityException("Не удалось создать вопрос.", t));
    }

    @PostMapping("/sequence")
    public Question createSequence(@Valid @RequestBody QuestionRequests.CreateSequenceQuestion question) {
        return questionRepository.create(question)
            .getOrElseThrow(t -> new CreateEntityException("Не удалось создать вопрос.", t));
    }

    @PostMapping("/match")
    public Question createMatch(@Valid @RequestBody QuestionRequests.CreateMatchQuestion question) {
        return questionRepository.create(question)
            .getOrElseThrow(t -> new CreateEntityException("Не удалось создать вопрос.", t));
    }

    @DeleteMapping("/{id}")
    public Question deleteById(@PathVariable("id") Long id) {
        return questionRepository.deleteById(id)
            .getOrElseThrow(() -> new NotFoundException("Вопрос " + id + "не найден."));
    }

}
