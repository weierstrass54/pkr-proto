package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.exception.UpdateEntityException;
import com.ckontur.pkr.exam.model.Qualification;
import com.ckontur.pkr.exam.repository.QualificationRepository;
import com.ckontur.pkr.exam.web.CreateOrUpdateByNameRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Квалификации ПКР"})
@RestController
@RequestMapping("/qualification")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
@Timed(value = "requests.qualification", percentiles = {0.75, 0.9, 0.95, 0.99})
public class QualificationController {
    private final QualificationRepository qualificationRepository;

    @GetMapping("/list")
    public List<Qualification> getAll() {
        return qualificationRepository.findAll();
    }

    @PostMapping("/")
    public Qualification create(@Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return qualificationRepository.create(request.getName())
            .getOrElseThrow(() -> new CreateEntityException("Не удалось создать новую квалификацию."));
    }

    @PutMapping("/{id}")
    public Qualification change(@PathVariable("id") Long id, @Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return qualificationRepository.updateById(id, request.getName())
            .getOrElseThrow(t -> new UpdateEntityException(t.getMessage(), t))
            .getOrElseThrow(() -> new NotFoundException("Уровень квалификации " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Qualification delete(@PathVariable("id") Long id) {
        return qualificationRepository.deleteById(id)
            .getOrElseThrow(() -> new NotFoundException("Квалификация " + id + " не найдена."));
    }
}
