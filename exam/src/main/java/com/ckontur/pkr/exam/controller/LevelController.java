package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.common.exception.UpdateEntityException;
import com.ckontur.pkr.exam.model.Level;
import com.ckontur.pkr.exam.repository.LevelRepository;
import com.ckontur.pkr.exam.web.CreateOrUpdateByNameRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Уровни квалификаций ПКР"})
@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
@Timed(value = "requests.level", percentiles = {0.75, 0.9, 0.95, 0.99})
public class LevelController {
    private final LevelRepository levelRepository;

    @GetMapping("/list")
    public List<Level> getAll() {
        return levelRepository.findAll();
    }

    @PostMapping("/")
    public Level create(@Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return levelRepository.create(request.getName())
            .getOrElseThrow(() -> new CreateEntityException("Не удалось создать новый уровень квалификации."));
    }

    @PutMapping("/{id}")
    public Level change(@PathVariable("id") Long id, @Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return levelRepository.updateById(id, request.getName())
            .getOrElseThrow(t -> new UpdateEntityException(t.getMessage(),t))
            .getOrElseThrow(() -> new NotFoundException("Уровень квалификации " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Level delete(@PathVariable("id") Long id) {
        return levelRepository.deleteById(id)
            .getOrElseThrow(() -> new NotFoundException("Уровень квалификации " + id + " не найден."));
    }
}
