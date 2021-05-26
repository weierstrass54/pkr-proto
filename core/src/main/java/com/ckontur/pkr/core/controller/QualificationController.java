package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.core.model.Qualification;
import com.ckontur.pkr.core.repository.QualificationRepository;
import com.ckontur.pkr.core.web.CreateOrUpdateByNameRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        return qualificationRepository.getAll();
    }

    @PostMapping("/")
    public Qualification create(@Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return qualificationRepository.create(request.getName())
            .orElseThrow(() -> new CreateEntityException("Не удалось создать новую квалификацию."));
    }

    @PutMapping("/{id}")
    public Qualification change(@PathVariable("id") Long id, @Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return qualificationRepository.updateById(id, request.getName())
            .orElseThrow(() -> new NotFoundException("Квалификация " + id + " не найдена."));
    }

    @DeleteMapping("/{id}")
    public Qualification delete(@PathVariable("id") Long id) {
        return qualificationRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Квалификация " + id + " не найдена."));
    }
}
