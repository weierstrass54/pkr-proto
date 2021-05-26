package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.core.model.Region;
import com.ckontur.pkr.core.repository.RegionRepository;
import com.ckontur.pkr.core.web.CreateOrUpdateByNameRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"Регионы ПКР"})
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
@Timed(value = "requests.region", percentiles = {0.75, 0.9, 0.95, 0.99})
public class RegionController {
    private final RegionRepository regionRepository;

    @GetMapping("/list")
    public List<Region> getAll() {
        return regionRepository.getAll();
    }

    @PostMapping("/")
    public Region create(@Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return regionRepository.create(request.getName())
            .orElseThrow(() -> new CreateEntityException("Не удалось создать новый регион."));
    }

    @PutMapping("/{id}")
    public Region change(@PathVariable("id") Long id, @Valid @RequestBody CreateOrUpdateByNameRequest request) {
        return regionRepository.updateById(id, request.getName())
            .orElseThrow(() -> new NotFoundException("Регион " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Region delete(@PathVariable("id") Long id) {
        return regionRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Регион " + id + " не найден."));
    }
}
