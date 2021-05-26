package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.core.model.Level;
import com.ckontur.pkr.core.repository.LevelRepository;
import com.ckontur.pkr.core.web.CreateOrUpdateByNameRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        return levelRepository.getAll();
    }

    @PostMapping("/")
    public Level create(@Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return levelRepository.create(request.getName())
            .orElseThrow(() -> new CreateEntityException("Не удалось создать новый уровень квалификации."));
    }

    @PutMapping("/{id}")
    public Level change(@PathVariable("id") Long id, @Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return levelRepository.updateById(id, request.getName())
            .orElseThrow(() -> new NotFoundException("Уровень квалификации " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Level delete(@PathVariable("id") Long id) {
        return levelRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Уровень квалификации " + id + " не найден."));
    }
}